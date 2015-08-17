package net.io.mina.server.service;

import net.io.mina.DispatchPacket;
import net.io.mina.Packet;
import net.io.mina.server.IOLog;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.SessionManager;
import net.io.mina.server.session.ClientSession;

import org.apache.commons.configuration.Configuration;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

public abstract class AbstractClientSessionService implements ClientSessionService {

	protected static final String ADDRESS = "address";
	
	protected static final String PORT = "port";

	protected SocketAcceptor acceptor;
	
	protected PacketHandler handler;
	
	protected String address;
	
	protected int port;
	
	protected SessionManager sessionManager;
	
	protected IOLog log;
	
	public AbstractClientSessionService(Configuration config, PacketHandler handler, SessionManager sessionManager) {
		this(config.getString(ADDRESS), config.getInt(PORT), handler, sessionManager);
	}
	
	public AbstractClientSessionService(String address, int port, PacketHandler handler, SessionManager sessionManager) {
		this.address = address;
		this.port = port;
		this.handler = handler;
		this.sessionManager = sessionManager;
	}

	public void close() {
		if (acceptor != null) {
			acceptor.unbindAll();
		}
	}
	
	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getEncryptionKey() {
		return null;
	}

	@Override
	public int getEncryptionLen() {
		return 16;
	}
	
	protected ProtocolCodecFilter createProtocolCodecFilter() {
		return new ProtocolCodecFilter(DefaultEncoder.class, DefaultDecoder.class);
	}
	
	@Override
	public void setIOLog(IOLog log) {
		this.log = log;
	}

	@Override
	public void addClientSession(ClientSession session) {
		sessionManager.add(session);
	}

	public static final class DefaultEncoder extends ProtocolEncoderAdapter {

		@Override
		public void encode(IoSession paramIoSession, Object paramObject,
				ProtocolEncoderOutput paramProtocolEncoderOutput) throws Exception {
			if (paramObject instanceof DispatchPacket) {
				DispatchPacket dp = (DispatchPacket) paramObject;
				Packet packet = dp.getPacket();
				ByteBuffer data = packet.getData();
				data.flip();
				int len = 8 + data.remaining();
				ByteBuffer buf = ByteBuffer.allocate(len);
//				buf.put(DispatchPacket.HEAD);
				buf.putInt(len);
				buf.putInt(dp.getId());
//				buf.put(Packet.HEAD);
				buf.putInt(len);
				buf.putInt(packet.getOpCode());
				buf.put(data);
				buf.flip();
				paramProtocolEncoderOutput.write(buf);
			}
		}
		
	}
	
	public static final class DefaultDecoder extends ProtocolDecoderAdapter {

		private static final String BUFFER = ".UABuffer";
		
		private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);

		@Override
		public void decode(IoSession paramIoSession, ByteBuffer paramByteBuffer,
				ProtocolDecoderOutput paramProtocolDecoderOutput) throws Exception {
			boolean useSessionBuffer = false;
			ByteBuffer buf = (ByteBuffer) paramIoSession.getAttribute(BUFFER);
			if (buf != null) {
				buf.put(paramByteBuffer);
				buf.flip();
				useSessionBuffer = true;
			} else {
				buf = paramByteBuffer;
			}
			
			while (true) {
				int pos = buf.position();
				if (buf.remaining() > 0) {
					int len = buf.getInt();
					int sessionId = buf.getInt();
					if (buf.remaining() > len - 8) {
						int packetLen = buf.getInt();
						int opCode = buf.getInt();
						ByteBuffer data = EMPTY;
						byte[] bytes = new byte[packetLen];
						buf.get(bytes);
						data = ByteBuffer.wrap(bytes);
						Packet packet = new Packet(opCode, data);
						DispatchPacket dp = new DispatchPacket(sessionId,packet);
						paramProtocolDecoderOutput.write(dp);
					} else {
						buf.position(pos);
						continue;
					}
				} else {
					break;
				}
			}
			
			if (buf.hasRemaining()) {
				storeRemainingInSession(buf, paramIoSession);
			} else {
				if (useSessionBuffer) {
					paramIoSession.setAttribute(BUFFER, null);
				}
			}
		}
		
	    private void storeRemainingInSession(ByteBuffer buf, IoSession session) {
	        ByteBuffer remainingBuf = ByteBuffer.allocate(buf.capacity());
	        remainingBuf.setAutoExpand(true);
	        remainingBuf.order(buf.order());
	        remainingBuf.put(buf);
	        session.setAttribute(BUFFER, remainingBuf);
	    }
		
	}
	
}
