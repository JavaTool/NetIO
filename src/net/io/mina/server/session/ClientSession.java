package net.io.mina.server.session;

import java.util.concurrent.atomic.AtomicInteger;

import net.io.mina.Packet;
import net.io.mina.server.Client;
import net.io.mina.server.Identity;
import net.io.mina.server.PacketHandler;

import org.apache.mina.common.IoSession;

/**
 * @brief �˽ӿڴ��һ���ͻ��˵����ӣ�һ���ͻ��˸�������˽�����һ�����ӣ���ô��Ӧ�ĻὨ��һ���˽ӿ�ʵ�����ʵ��
 * @note �ͻ��������ڷ������ϵĶ�Ӧ�壬ͨ��˶�Ӧ�壬���������ܹ����ܿͻ��˷��������İ����·����ͻ��ˣ�����֪���ͻ������ӵ�״̬
 * @author Jeffrey
 */
public interface ClientSession {
	
	AtomicInteger id_gen = new AtomicInteger(0);
	
	/**
	 * @note ���ӵ�Id�ţ�ÿ�����ӵ�Id�Ŷ��ǲ���ͬ�ģ�������ļ��ľ����Щ����
	 * @return ���ӵ�Id��
	 */
	int getId();
	/**
	 * @note ��ǰ�����Ƿ�������״̬
	 */
	boolean isConnected();
	/**
	 * @note ���Ͱ��ͻ���
	 * 
	 * @param packet Ҫ���͵İ�
	 */
	void send(Packet packet);
	/**
	 * @note �ر�����
	 */
	void close();
	/**
	 * @note ��ȡ�����ӵİ�����
	 */
	PacketHandler getHandler();
	/**
	 * @note ��ȡClientSession��Ӧ��Client��Client�������κ�ʵ��Client�ӿڵĶ��󣬱�����������Ժ�Client������Է���Player
	 */
	Client getClient();
	/**
	 * @note ���õ�ǰ��Client����
	 */
	void setClient(Client client);
	/**
	 * @note ����ClientSession
	 */
	void update();
	
	void keepLive();
	/**
	 * @note ��Ӱ�һ�������������ȡ���ͻ��˰��Ժ��ôη�����ӵ����ܶ�����
	 * @param packet �������յ��Ŀͻ����ϴ��İ�
	 */
	void addPacket(Packet packet);
	/**
	 * @note ����һ��Identity����һ����������֤�ɹ��Ժ������˺Ŷ���ʹ��
	 * @throws SecurityException ���ǰClientSession��״̬���󣬱����Ѿ�������֤�ɹ�״̬���׳�SecurityException
	 */
	void authenticate(Identity identity) throws SecurityException;
	/**
	 * @note ��ȡ��ǰ����֤����
	 */
	Identity getIdentity();
	/**
	 * @note ȡ�ͻ���IP��ַ
	 */
	String getClientIP();
	/**
	 * @note ����Ƿ������¼��������������������¼��
	 */
	boolean checkOnlineCount(int currentLoginedAccounts);
	
	IoSession getIoSession();
	
}
