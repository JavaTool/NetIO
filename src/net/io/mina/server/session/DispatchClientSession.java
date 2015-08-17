package net.io.mina.server.session;

import net.io.mina.DispatchPacket;
import net.io.mina.Packet;
import net.io.mina.server.IOLog;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.service.DispatchClientSessionService;
import net.io.util.IOUtil;

import org.apache.mina.common.IoSession;

/**
 * ͨ�������������ӵ��û��Ự��
 * @author 	lighthu
 */
public abstract class DispatchClientSession extends AbstractClientSession {
	
	/*
	 * �ỰID����32λ�Ǵ������������ID����32λ�Ǵ���������ỰID
	 */
	/**
	 * �ر�ʱ�Ƿ���Ҫ֪ͨPROXY
	 */
	protected boolean notify;
	/**
	 * IP��ַ
	 */
	protected String ip = "";
	
	protected int head; //����Ǹ�proxy���������ӵ�Id��ÿ��һ��proxy���Ӿ�����1
	
	protected int proxyGenId; //�����proxy�����Session�����Id��Ҳ����proxy��ͻ������ӷ����Id

	public DispatchClientSession(int id, DispatchClientSessionService service,
			IoSession session, PacketHandler handler,int head,int proxyGenId, IOLog log) {
		super(service, session, handler, id, log);
		this.head = head;
		this.notify = true;
		this.proxyGenId = proxyGenId;
		service.addClientSession(this);
	}

	@Override
	public void send(Packet packet) {
		if (state == State.CONNECTED || state == State.AUTHENTICATED) {
			if (packet.needEncrypt) {
				packet = encrypt(packet);
			}
			DispatchPacket dp = new DispatchPacket(proxyGenId, packet);
			session.write(dp);
		}
	}

	@Override
	public void close() {
		super.close();
		if (notify) {
			// ��proxy����PROXY_SESSION_DISCONNECT��֪ͨ�ر�����
			sessionDisconnect();
//			Packet packet = new Packet(OpCode.PROXY_SESSION_DISCONNECT);
//			session.write(new DispatchPacket(proxyGenId, packet));
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
//	@Override
//    public boolean checkOnlineCount(int currentLoginedAccounts) {
//        SyncInteger ati = (SyncInteger)session.getAttribute(DispatchClientSessionService.SESSION_COUNTER);
//        int count = ati.get();
//        ProxyManagingService pms = Platform.getAppContext().get(ProxyManagingService.class);
//        if (pms != null) {
//            return count <= pms.getMaxPlayer(session);
//        }
//        return true;
//    }
    
    public long getFullId(){
    	return (((long) proxyGenId) << 32) | head;
    }

	public int getHead() {
		return head;
	}

	public int getProxyGenId() {
		return proxyGenId;
	}
}
