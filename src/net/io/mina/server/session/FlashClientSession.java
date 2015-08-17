package net.io.mina.server.session;

import java.net.InetSocketAddress;

import net.io.mina.server.IOLog;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.service.FlashClientSessionService;

import org.apache.mina.common.IoSession;


public class FlashClientSession extends AbstractClientSession {
	
	protected String ip;
	
	public FlashClientSession(FlashClientSessionService service, IoSession session, PacketHandler handler, IOLog log){
		super(service, session, handler, id_gen.incrementAndGet(), log);
		InetSocketAddress addr = (InetSocketAddress)session.getRemoteAddress();
		ip = addr.getAddress().getHostAddress();
	}

	@Override
	public void close() {
		super.close();
		session.close();
		state = State.DISCONNECTED;
	}

	@Override
	public boolean isConnected() {
		return state==State.CONNECTED||state==State.AUTHENTICATED;
	}
	
	/**
	 * ȡ�ͻ���IP��ַ
	 */
	@Override
	public String getClientIP() {
		return ip;
	}

    /**
     * ����Ƿ������¼��������������������¼��
     */
	@Override
    public boolean checkOnlineCount(int currentLoginedAccounts) {
        return true;
    }
	
}

