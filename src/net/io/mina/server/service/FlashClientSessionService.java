package net.io.mina.server.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;

import net.io.mina.Packet;
import net.io.mina.server.MinaUADecoder;
import net.io.mina.server.MinaUAEncoder;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.SessionManager;
import net.io.mina.server.session.ClientSession;
import net.io.mina.server.session.FlashClientSession;

import org.apache.commons.configuration.Configuration;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class FlashClientSessionService extends AbstractClientSessionService {
	
//	private static final Charset utf8 = Charset.forName("utf-8");

//	private static final String security_req = "<policy-file-request/>";
	
	private static final String allow_all = "<cross-domain-policy>\n"
			+ "  <allow-access-from  domain=\"*\"  to-ports=\"*\"  />\n"
			+ "</cross-domain-policy>";
	
	protected static final IoFilter filter = new ProtocolCodecFilter(MinaUAEncoder.class,MinaUADecoder.class);
	
	protected LinkedHashMap<Integer,FlashClientSession> sessions = new LinkedHashMap<Integer,FlashClientSession>();
	
	public FlashClientSessionService(Configuration config, PacketHandler handler, SessionManager sessionManager) {
		super(config, handler, sessionManager);
	}
	
	public FlashClientSessionService(String address, int port, PacketHandler handler, SessionManager sessionManager) {
		super(address, port, handler, sessionManager);
	}
	
//	public void startup() throws Exception {
//		bind();
//	}

	public void bind() throws IOException{
		acceptor = new SocketAcceptor();
		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
//      cfg.getFilterChain().addLast( "codec", new ProtocolCodecFilter(FlashUAEncoder.class,FlashUADecoder.class));
        cfg.getFilterChain().addLast( "codec", createProtocolCodecFilter());
        acceptor.bind( new InetSocketAddress(address,port) , new FlashClientSessionHandler(), cfg);
	}
	
	@Override
	public void addClientSession(ClientSession session) {
		super.addClientSession(session);
		sessions.put(session.getId(),(FlashClientSession)session);
	}

	@Override
	public void removeClientSession(ClientSession session){
		sessions.remove(session);
	}
	
	protected class FlashClientSessionHandler extends IoHandlerAdapter{
		
		@Override
		public void exceptionCaught(IoSession session, Throwable t) throws Exception {
		    t.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object msg) throws Exception {
			FlashClientSession s = (FlashClientSession)session.getAttachment();
			if(s!=null){
				if(msg instanceof Packet) {
					s.addPacket((Packet)msg);
				} else if(msg instanceof ByteBuffer){
//					ByteBuffer buf = (ByteBuffer)msg;
//					int len = buf.remaining();
//			        byte[] data = new byte[len];
//			        buf.get(data);
//			        String str = new String(data, utf8);
//			        if(str.startsWith(security_req)){
//			        	byte[] reps = allow_all.getBytes("UTF-8");   
//			        	ByteBuffer ret = ByteBuffer.allocate(reps.length+1);   
//			        	ret.put(reps);   
//			        	ret.put((byte)0x0);   
//			        	ret.flip();
//			        	session.write(ret);
			        	session.write(allow_all);
//			        	if (session.getFilterChain().get("codec") == null) {
//			        		session.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaUAEncoder(), new MinaUADecoder()));
//				        	addClientSession(s);
//			        	}
//			        }
				}
			}
		}
		
		@Override
		public void sessionClosed(IoSession session) throws Exception{
			FlashClientSession ds = (FlashClientSession)session.getAttachment();
			if(ds!=null){
				session.setAttachment(null);
				ds.setDisconnecting();
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			// ���IP��ַ
			if (!sessionManager.getTrustIp().isTrustIp((InetSocketAddress) session.getRemoteAddress())) {
				session.close();
				return;
			}
			
			FlashClientSession dSession = new FlashClientSession(FlashClientSessionService.this, session, handler, log);
			session.setAttachment(dSession);
			addClientSession(dSession);
		}
	}

	public FlashClientSession getClientSession(int id){
		return sessions.get(id);
	}
	
}
