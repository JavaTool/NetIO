package net.io.mina.server.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.io.mina.server.PacketHandler;
import net.io.mina.server.SessionManager;
import net.io.mina.server.session.AbstractClientSession;
import net.io.mina.server.session.ClientSession;
import net.io.mina.server.session.DispatchClientSession;
import net.io.util.SyncInteger;

import org.apache.commons.configuration.Configuration;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

/**
 * ������������ӷ���
 * @author 	lighthu
 */
public abstract class DispatchClientSessionService extends AbstractClientSessionService {
	
	public static final String SESSION_COUNTER = "SessionCounter";

	protected static final String SESSION_ID = "SESSION_ID";
	
	/**
	 * �����û��Ự��id�ĸ�32λ�Ǵ���ID����32λ�ǻỰID
	 */
	protected Map<Long, DispatchClientSession> sessions = new ConcurrentHashMap<Long, DispatchClientSession>();
	/**
	 * ����ID�����
	 */
	protected SyncInteger ids = new SyncInteger(0);

	public DispatchClientSessionService(Configuration config, PacketHandler handler, SessionManager sessionManager) {
		super(config, handler, sessionManager);
	}
	
	public DispatchClientSessionService(String address, int port, PacketHandler handler, SessionManager sessionManager){
		super(address, port, handler, sessionManager);
	}

	public void bind() throws IOException {
		acceptor = new SocketAcceptor();
		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		cfg.getFilterChain().addLast(
				"codec", createProtocolCodecFilter());
		acceptor.bind(new InetSocketAddress(address, port),  createIoHandlerAdapter(), cfg);
	}

	@Override
	public void close() {
		sessions.clear();
		super.close();
	}

	@Override
	public void addClientSession(ClientSession session) {
		super.addClientSession(session);
		sessions.put(((DispatchClientSession) session).getFullId(), (DispatchClientSession) session);

		// �޸����Ӽ�����
		try {
			SyncInteger ati = (SyncInteger) session.getIoSession().getAttribute(SESSION_COUNTER);
			ati.incrementAndGet();
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	public void removeClientSession(ClientSession session) {
		ClientSession s = sessions.remove(((DispatchClientSession) session).getFullId());
		if (s != null) { // �޸����Ӽ�����
			try {
				AbstractClientSession cs = (AbstractClientSession) session;
				SyncInteger ati = (SyncInteger) cs.getIoSession().getAttribute(SESSION_COUNTER);
				ati.decrementAndGet();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	protected DispatchClientSession getSession(long id) {
		return sessions.get(id);
	}
	
	protected abstract IoHandlerAdapter createIoHandlerAdapter();
	
	public PacketHandler getHandler(){
		return handler;
	}
	
}
