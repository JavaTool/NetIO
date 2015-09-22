package net.io.mina.server.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.io.mina.server.PacketHandler;
import net.io.mina.server.SessionManager;
import net.io.mina.server.session.AbstractClientSession;
import net.io.mina.server.session.AdminDispatchClientSession;
import net.io.mina.server.session.ClientSession;
import net.io.util.SyncInteger;

import org.apache.commons.configuration.Configuration;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public abstract class AdminDispatchClientSessionService extends AbstractClientSessionService implements Runnable {
	
	public static final String SESSION_COUNTER = "SessionCounter";

	protected static final String SESSION_ID = "SESSION_ID";
	
	protected Map<Long, AdminDispatchClientSession> sessions = new ConcurrentHashMap<Long, AdminDispatchClientSession>();
	
	protected SyncInteger ids = new SyncInteger(0);
	
	protected String proxyIP = "221.179.216.53";
	
	protected int proxyPort = 7001;
	
	protected String proxyPassword = "proxy@pip2008";
	
	protected int connectionCount = 0;
	
	protected Thread notifyProxyThread;
	
	protected boolean stopped = false;

	public AdminDispatchClientSessionService(Configuration config, PacketHandler handler, SessionManager sessionManager) {
		super(config, handler, sessionManager);
		String ip = config.getString("proxy_address");
		if (ip != null) {
			proxyIP = ip;
			proxyPort = config.getInt("proxy_port");
			proxyPassword = config.getString("proxy_password");
		}
	}
	
	public AdminDispatchClientSessionService(String address, int port, PacketHandler handler, SessionManager sessionManager){
		super(address, port, handler, sessionManager);
	}
	
	@Override
	public void run() {
		while (!stopped) {
			try {
				Thread.sleep(60000L);
			} catch (Exception e) {
			}
			if (connectionCount == 0) {
				try {
					notifyProxy();
				} catch (Exception e) {
				}
			}
		}
	}
	
	protected void notifyProxy() throws IOException {
		InetAddress addr = InetAddress.getByName(proxyIP);
    	String cmd = proxyPassword + ":addserver:" + address + ":" + port + ":3";
    	byte[] sendData = cmd.getBytes();
    	DatagramPacket dpack = new DatagramPacket(sendData, sendData.length, addr, proxyPort);
    	DatagramSocket localSocket = new DatagramSocket();
    	localSocket.send(dpack);
    	localSocket.close();
    	
		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;
    	try {
    		Thread.sleep(1000);
    		cmd = "REGSVR" + cmd;
    		sendData = cmd.getBytes("ASCII");
    		socket = new Socket(proxyIP, proxyPort);
    		is = socket.getInputStream();
    		os = socket.getOutputStream();
    		os.write(sendData);
    		byte[] buf = new byte[2];
    		new DataInputStream(is).read(buf);
    	} catch (Exception e) {
    		
    	} finally {
    		if (is != null) {
    			try {
    				is.close();
    			} catch (Exception e) {
    				
    			}
    		}
    		if (os != null) {
    			try {
    				os.close();
    			} catch (Exception e) {
    				
    			}
    		}
    		if (socket != null) {
    			try {
    				socket.close();
    			} catch (Exception e) {
    				
    			}
    		}
    	}
	}

	@Override
	public void bind() throws IOException {
		acceptor = new SocketAcceptor();
		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		cfg.getFilterChain().addLast(
				"codec", createProtocolCodecFilter());
//				new ProtocolCodecFilter(DispatchUAEncoder.class,
//						DispatchUADecoder.class));
		acceptor.bind(new InetSocketAddress(address, port), 
				createIoHandlerAdapter(), cfg);
	}

	@Override
	public void shutdown() {
		sessions.clear();
		super.shutdown();
		stopped = true;
		if (notifyProxyThread != null) {
			try {
				notifyProxyThread.interrupt();
			} catch (Exception e) {
				
			}
		}
	}

	@Override
	public void addClientSession(ClientSession session) {
		super.addClientSession(session);
		sessions.put(((AdminDispatchClientSession) session).getAdminId(), (AdminDispatchClientSession) session);

		// �޸����Ӽ�����
		try {
			AbstractClientSession cs = (AbstractClientSession) session;
			SyncInteger ati = (SyncInteger) cs.getIoSession().getAttribute(SESSION_COUNTER);
			ati.incrementAndGet();
		} catch (Exception e) {
			
		}
	}

	@Override
	public void removeClientSession(ClientSession session) {
		ClientSession s = sessions.remove(((AdminDispatchClientSession) session).getAdminId());
		if (s != null) {
			// �޸����Ӽ�����
			try {
				AbstractClientSession cs = (AbstractClientSession) session;
				SyncInteger ati = (SyncInteger) cs.getIoSession().getAttribute(SESSION_COUNTER);
				ati.decrementAndGet();
			} catch (Exception e) {
				
			}
		}
	}

	protected AdminDispatchClientSession getSession(long id) {
		return sessions.get(id);
	}
	
	protected abstract IoHandlerAdapter createIoHandlerAdapter();

//	class DispatchClientSessionHandler extends IoHandlerAdapter {
//		
//		@Override
//		public void exceptionCaught(IoSession session, Throwable t) throws Exception {
//			log.debug(t.getMessage(), t);
//		}
//
//		@Override
//		public void messageReceived(IoSession session, Object msg) throws Exception {
////			log.debug("receive msg");
//			if (msg instanceof DispatchPacket) {
//				DispatchPacket dp = (DispatchPacket) msg;
//				if (dp.id == -1) {
//					// ���ID��-1����ֻ������һ�ְ�PROXY_LOGIN
//					if (dp.packet.opCode == OpCode.PROXY_LOGIN) {
//						int ip = dp.packet.getInt();
//						int port = dp.packet.getShort();
//					}
//				} else {
//					int sessionId = (Integer) session.getAttribute(SESSION_ID);
//					long fullId = (((long) sessionId) << 32) | dp.id;
//					AdminDispatchClientSession ds = getSession(fullId);
//					if (ds == null) {
//						ds = new AdminDispatchClientSession(fullId, 
//								AdminDispatchClientSessionService.this, session, handler);
////						addClientSession(ds); 
//					}
//					switch (dp.packet.opCode) {
//					case OpCode.PROXY_SYNC_IP: // ͬ��IP
//						ds.setIP(dp.packet.getInt());
//						break;
//					case OpCode.PROXY_SESSION_DISCONNECT: // �Ͽ�����
////						log.info("session disconnect");
//						ds.silentClose();
//						ds.setDisconnecting();
//						break;
//					default:
////						System.out.println("recv: " + dp.packet.opCode);
//						ds.addPacket(dp.packet);
//						break;
//					}
//				}
//			}
//		}
//
//		@Override
//		public void sessionClosed(IoSession session) throws Exception {
//			// proxy���ӶϿ����ر�����ͨ���proxy���ӵĻỰ
//			for (AdminDispatchClientSession ds : sessions.values()) {
//				if (ds.session == session) {
//					ds.silentClose();
//					ds.setDisconnecting();;
//				}
//			}
//			synchronized (this) {
//				connectionCount--;
//			}
//		}
//
//		@Override
//		public void sessionCreated(IoSession session) throws Exception {
//			// ���IP��ַ
//			TrustIpService ts = Platform.getAppContext().get(TrustIpService.class);
//			if (!ts.isTrustIp((InetSocketAddress)session.getRemoteAddress())) {
//				session.close();
//				return;
//			}
//			
//			session.setAttribute(SESSION_ID, new Integer(ids.incrementAndGet()));
//			session.setAttribute(SESSION_COUNTER, new SyncInteger(0));
//			synchronized (this) {
//				connectionCount++;
//			}
//		}
//	}
	
}
