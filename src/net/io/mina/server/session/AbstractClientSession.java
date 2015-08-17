package net.io.mina.server.session;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.io.mina.Packet;
import net.io.mina.server.Client;
import net.io.mina.server.IOLog;
import net.io.mina.server.Identity;
import net.io.mina.server.PacketHandler;
import net.io.mina.server.service.ClientSessionService;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

public abstract class AbstractClientSession implements ClientSession {
	
	public static final AtomicInteger id_gen = new AtomicInteger(0);
	
	protected static final long DISCONNECTING_TIME = 4 * 60 * 1000L;

	protected static Random RND = new Random();
	
	protected enum State{CONNECTED, DISCONNECTING, DISCONNECTED, AUTHENTICATED, DISCHARGED};
	
	protected volatile State state;
	
	protected IoSession session;
	
	protected PacketHandler handler;

	protected Client client;
	
	protected ArrayBlockingQueue<Packet> queue;
	
	protected ClientSessionService service;
	
	protected Identity identity;
	
	protected int id, encryptLen; // ÿ������ܵĳ���
	
	protected long lastReceiveStamp, disconnectedStamp;
	// �Ự��Կ
	protected byte[] sessionKey;
	
	protected IOLog log;
	
	public AbstractClientSession(ClientSessionService service, IoSession session, PacketHandler handler, int id, IOLog log) {
		this.id = id;
		this.service = service;
		this.session = session;
		this.handler = handler;
		this.log = log;
		
		queue = new ArrayBlockingQueue<Packet>(1024);
		lastReceiveStamp = System.currentTimeMillis();
		disconnectedStamp = 0L;
		state = State.CONNECTED;
	}

	@Override
	public void send(Packet packet){
		if (state == State.CONNECTED || state == State.AUTHENTICATED) {
			if (packet.needEncrypt) {
				packet = encrypt(packet);
			}
			session.write(packet);
		}
	}

	@Override
	public PacketHandler getHandler(){
		return handler;
	}

	@Override
	public IoSession getIoSession(){
		return session;
	}

	@Override
	public Client getClient() {
		if (client == null) {
			log.info("[SESSION_DIE]SESID[" + this.getId() + "]");
		}
		return client;
	}

	@Override
	public void setClient(Client client) {
		this.client = client;
		if (client != null) {
			client.setSession(this);
		}
	}

	@Override
	public void keepLive() {
		lastReceiveStamp = System.currentTimeMillis();
	}

	@Override
	public void addPacket(Packet packet){
		if(packet.needEncrypt){
			decrypt(packet);
		}
		queue.offer(packet);
		lastReceiveStamp = System.currentTimeMillis();
	}
	
	@Override
	public void update() {
		if ((state != State.DISCONNECTING && state != State.DISCONNECTED) && (System.currentTimeMillis() - lastReceiveStamp) > DISCONNECTING_TIME) {
			setDisconnecting();
		}
		
		Packet packet = null;
		PacketHandler handler = getHandler();
		while ((packet = queue.poll()) != null) {
			try {
				handler.handle(packet, this);
			} catch (Exception e) {
				log.error(e);
			}
		}
		
		if (state == State.DISCONNECTED) {
			service.removeClientSession(this);
		}
		if (state == State.DISCONNECTING) {
			setDisconnected();
		}
	}
	
	protected void cleanMessageQueue() {
		queue.clear();
	}

	@Override
	public void close() {
		if (isConnected()) {
			cleanMessageQueue();
			setDisconnecting();
		}
	}
	
	public void setDisconnecting() {
		state = State.DISCONNECTING;
		if (client != null) {
			client.setDisconnecting();
		}
	}
	
	protected void setDisconnected() {
		state = State.DISCONNECTED;
		disconnectedStamp = System.currentTimeMillis();
	}

	@Override
	public void authenticate(Identity identity) throws SecurityException {
		this.identity = identity;
		state = State.AUTHENTICATED;
	}

	@Override
	public Identity getIdentity() {
		return identity;
	}

	@Override
	public int getId() {
		return id;
	}

	/**
	 * ��ɻỰ���벢�·��������ӽ���ʱ���ô˷������ɽ��Ự����Ϊ���ܻỰ��
	 */
	public void setupEncryption() throws IOException {
		String encryptionKey = service.getEncryptionKey();
		if (encryptionKey != null) {
			sessionKey = new byte[8];
			RND.nextBytes(sessionKey);
			encryptLen = service.getEncryptionLen();
			Packet packet = client.createSessionKeyPacket();
			byte[] ekey = new byte[sessionKey.length];
			System.arraycopy(sessionKey, 0, ekey, 0, sessionKey.length);
			byte[] pkey = encryptionKey.getBytes("ASCII");
			for (int i = 0; i < sessionKey.length; i++) {
				int keyInd = i % pkey.length;
				ekey[i] ^= pkey[keyInd];
			}
			packet.put(ekey);
			packet.putInt(encryptLen);
			packet.needEncrypt = false;
			send(packet);
		}
	}

	/**
	 * ��һ������м��ܣ�ʹ�ûỰ��Կ����
	 * @param	packet
	 * 			��
	 */
	protected Packet encrypt(Packet packet) {
		if (sessionKey == null) {
			return packet;
		}
		ByteBuffer data = packet.getData();
		data.flip();
		int dlen = data.remaining();
		byte[] buf = new byte[dlen];
		data.get(buf);
		int keyLen = sessionKey.length;
		for (int i = 0; i < dlen && i < encryptLen; i++) {
			buf[i] ^= sessionKey[i % keyLen];
		}
		ByteBuffer ret = ByteBuffer.allocate(dlen);
		ret.put(buf);
		return new Packet((short) (packet.getOpCode() | 0x4000), ret);
	}
	
	/**
	 * ��һ������н��ܣ�ʹ�ûỰ��Կ����
	 * @param	packet
	 * 			��
	 */
	protected void decrypt(Packet packet) {
		if (sessionKey == null) {
			return;
		}
		ByteBuffer data = packet.getData();
		int dlen = data.remaining();
		byte[] buf = new byte[dlen];
		data.get(buf);
		int keyLen = sessionKey.length;
		for (int i = 0; i < dlen && i < encryptLen; i++) {
			buf[i] ^= sessionKey[i % keyLen];
		}
		data.position(0);
		data.put(buf);
		data.flip();
	}
	
}
