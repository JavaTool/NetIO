package net.io.mina.server.session;

import net.io.mina.DispatchPacket;
import net.io.mina.Packet;
import net.io.mina.server.IOLog;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.service.AdminDispatchClientSessionService;
import net.io.util.IOUtil;

import org.apache.mina.common.IoSession;

/**
 * ͨ�������������ӵĹ���Ự��
 * @author 	lighthu
 */
public abstract class AdminDispatchClientSession extends AbstractClientSession {
	
	/**
	 * �ỰID����32λ�Ǵ������������ID����32λ�Ǵ���������ỰID
	 */
	protected long id;
	/**
	 * �ر�ʱ�Ƿ���Ҫ֪ͨPROXY
	 */
	protected boolean notify;
	/**
	 * IP��ַ
	 */
	protected String ip = "";

	public AdminDispatchClientSession(long id, AdminDispatchClientSessionService service, IoSession session, PacketHandler handler, IOLog log) {
		super(service, session, handler, id_gen.incrementAndGet(), log);
		
		this.id = id;
		notify = true;
		service.addClientSession(this);
	}

	@Override
	public void send(Packet packet) {
		if (state == State.CONNECTED || state == State.AUTHENTICATED) {
			session.write(new DispatchPacket((int) id, packet));
//			log.info("send:" + packet.opCode);
		}
	}

	@Override
	public void close() {
		super.close();
		if (notify) {
			// ��proxy����PROXY_SESSION_DISCONNECT��֪ͨ�ر�����
			sessionDisconnect();
//			Packet packet = new Packet(OpCode.PROXY_SESSION_DISCONNECT);
//			session.write(new DispatchPacket((int) id, packet));
		}
	}
	
	protected abstract void sessionDisconnect();
	
	public void silentClose() {
		notify = false;
	}

	@Override
	public boolean isConnected() {
		return state == State.CONNECTED || state == State.AUTHENTICATED;
	}
	
	/**
	 * ȡ�ͻ���IP��ַ
	 */
	@Override
	public String getClientIP() {
		return ip;
	}
	
	/**
	 * ����IP��ַ
	 * @param ip
	 */
	public void setIP(int ipnum) {
		ip = IOUtil.ip2str(ipnum);
	}

    /**
     * ����Ƿ������¼��������������������¼��
     */
	@Override
    public boolean checkOnlineCount(int currentLoginedAccounts) {
        return true;
    }
	
	public long getAdminId() {
		return id;
	}
	
}
