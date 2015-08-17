package net.io.mina.server;

import java.io.IOException;

import net.io.mina.DispatchPacket;
import net.io.mina.Packet;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class DispatchUADecoder extends ProtocolDecoderAdapter {
	
	protected static final String BUFFER = ".UABuffer";
	
	protected static final ByteBuffer EMPTY = ByteBuffer.allocate(0);
	
	@Override
	public void decode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
		boolean useSessionBuffer = false;
		ByteBuffer buf = (ByteBuffer)session.getAttribute(BUFFER);
		if (buf != null) {
			buf.put(in);
			buf.flip();
			useSessionBuffer = true;
		} else {
			buf = in;
		}
		for (;;) {
			if (buf.remaining() > 10) {
				int pos = buf.position();
				if (buf.get() == 68 && buf.get() == 65) { //'D'&'A'
					int len = buf.getInt();
					int sessionId = buf.getInt();
					if (buf.remaining() >= (len - 10)) {  //ȥ��head�Լ�lenһ��10���ֽ�
						if (buf.get() == 85 && buf.get() == 65) { // 'U'&'A'
							int packetLen = buf.getInt();
							int opCode = buf.getInt();
							ByteBuffer data = EMPTY;
							byte[] bytes = new byte[packetLen - 10];
							buf.get(bytes);
							data = ByteBuffer.wrap(bytes);
							Packet packet = new Packet(opCode, data);
							DispatchPacket dp = new DispatchPacket(sessionId, packet);
							out.write(dp);
						} else {
							session.setAttribute(BUFFER,null);
							throw new IOException("UA head error");
						}
					} else {
						buf.position(pos);
						break;
//						buf.compact();
//						if(!useSessionBuffer){
//							session.setAttribute(BUFFER,buf);
//							break;
//						}
					}
				} else {
					buf.position(pos + 1);
					continue;
					//session.setAttribute(BUFFER,null);
					//throw new IOException("DA head error.");
				}
			} else {
//				if(buf.hasRemaining()){
//					buf.compact();
//					if(!useSessionBuffer)
//						session.setAttribute(BUFFER,buf);
//				}
				break;
			}
		}
		if (buf.hasRemaining()) {
			storeRemainingInSession(buf,session);
		} else {
			if (useSessionBuffer) {
				session.setAttribute(BUFFER,null);
			}
		}
	}
	
    protected void storeRemainingInSession(ByteBuffer buf, IoSession session) {
        ByteBuffer remainingBuf = ByteBuffer.allocate(buf.capacity());
        remainingBuf.setAutoExpand(true);
        remainingBuf.order(buf.order());
        remainingBuf.put(buf);
        session.setAttribute(BUFFER, remainingBuf);
    }
    
}
