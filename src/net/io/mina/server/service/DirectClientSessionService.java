package net.io.mina.server.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

import net.io.mina.Packet;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.SessionManager;
import net.io.mina.server.session.ClientSession;
import net.io.mina.server.session.DirectClientSession;

import org.apache.commons.configuration.Configuration;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.google.common.collect.Maps;

public class DirectClientSessionService extends AbstractClientSessionService {
	
	protected Map<Integer, DirectClientSession> sessions = Maps.newHashMap();
	
	public DirectClientSessionService(Configuration config, PacketHandler handler, SessionManager sessionManager) {
		super(config, handler, sessionManager);
	}
	
	public DirectClientSessionService(String address, int port, PacketHandler handler, SessionManager sessionManager) {
		super(address, port, handler, sessionManager);
	}
	
//	public void startup() throws Exception {
//		bind();
//	}

	@Override
	public void bind() throws IOException{
		acceptor = new SocketAcceptor();
		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
//      cfg.getFilterChain().addLast( "codec", new ProtocolCodecFilter(MinaUAEncoder.class, MinaUADecoder.class));
        cfg.getFilterChain().addLast( "codec", createProtocolCodecFilter());
        acceptor.bind( new InetSocketAddress(address, port), new DirectClientSessionHandler(), cfg);
	}
	
	@Override
	public void addClientSession(ClientSession session){
		super.addClientSession(session);
		sessions.put(session.getId(), (DirectClientSession) session);
	}

	@Override
	public void removeClientSession(ClientSession session){
		sessions.remove(session.getId());
	}
	
	protected class DirectClientSessionHandler extends IoHandlerAdapter {
		
		@Override
		public void exceptionCaught(IoSession session, Throwable t) throws Exception {
		    t.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object msg) throws Exception {
			DirectClientSession s = (DirectClientSession)session.getAttachment();
			if (s != null && msg instanceof Packet) {
				s.addPacket((Packet)msg);
			}
		}
		
		@Override
		public void sessionClosed(IoSession session) throws Exception{
			DirectClientSession ds = (DirectClientSession) session.getAttachment();
			if (ds != null) {
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
			
			DirectClientSession dSession = new DirectClientSession(DirectClientSessionService.this, session, handler, log);
			session.setAttachment(dSession);
			addClientSession(dSession);
		}
		
	}
	
	public ClientSession getClientSession(int id){
		return sessions.get(id);
	}
	
	public int getSessionSize() {
		return sessions.size();
	}
	
}
