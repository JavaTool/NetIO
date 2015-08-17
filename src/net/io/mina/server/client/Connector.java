package net.io.mina.server.client;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.io.mina.DispatchPacket;
import net.io.mina.Packet;

import org.apache.log4j.Logger;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

public abstract class Connector {
	
	protected static final Logger log = Logger.getLogger(Connector.class);
	
	protected SocketAddress address;
	
	protected SocketConnector connector;
	
	protected SocketConnectorConfig config;

	protected int receiveBufferSize = 32767, sendBufferSize = 32767;
	
	protected String id;
	
	protected boolean connected = false, valid = false, needSecureAuth, needRetry = true;
	
	protected IoHandler minaHandler = new OriginalSessionHandler();
	
	protected Object lock = new Object();
	
	protected IoSession session;
	
	protected ExecutorService executor;
	
	protected ConnectFuture connectFuture;
	
	public Connector(String id, SocketAddress address, boolean needSecureAuth) {
		this.id = id;
		this.address = address;
		this.needSecureAuth = needSecureAuth;
		initConnector();
	}
	
	private void initConnector() {
		executor = Executors.newCachedThreadPool();
		connector = new SocketConnector(1, executor);
		config = new SocketConnectorConfig();
		config.setThreadModel(ThreadModel.MANUAL);
		config.getSessionConfig().setTcpNoDelay(true);
		config.getSessionConfig().setSendBufferSize(sendBufferSize);
		config.getSessionConfig().setReceiveBufferSize(receiveBufferSize);
		init();
	}
	
	protected abstract void init();
	
	protected abstract void handle(DispatchPacket dp);
	
	public void connect() throws ConnectException {
		if ((connected) || (valid)) {
			throw new IllegalStateException("connection is connected");
		} else {
			connectFuture = connector.connect(address, minaHandler, config);
			synchronized (lock) {
				try {
					lock.wait(10000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!valid) {
					if (session != null) {
						session.close();
					}
					connected = false;
					throw new ConnectException("connect [" + address + "] error");
				}
			}
		}
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void close() throws Exception {
		setNeedRetry(false);
		if (session != null && session.isConnected()) {
			session.getCloseFuture().setClosed();
			session.close();
		}
		executor.shutdown();
		config.getFilterChain().clear();
	}
	
	public SocketAddress getRemoteAddress() {
		return session.getRemoteAddress();
	}
	
	public void send(Object object) {
		session.write(object);
	}
	
	public void send(int id, Packet packet) {
		session.write(new DispatchPacket(id, packet));
	}
	
	public void setNeedRetry(boolean needRetry) {
		this.needRetry = needRetry;
	}
	
	public boolean isNeedRetry() {
		return needRetry;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	protected class OriginalSessionHandler extends IoHandlerAdapter {
		
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			Connector.log.info(cause, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			if (message instanceof DispatchPacket) {
				Connector.this.handle((DispatchPacket) message);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			Connector.this.connected = false;
			if (Connector.this.needRetry && Connector.this.valid) {
				Connector.this.valid = false;
				while (!Connector.this.connected) {
					try {
						Thread.sleep(3000L);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					try {
						Connector.this.initConnector();
						Connector.this.connect();
						Connector.log.info("breaked");
					} catch (Exception e) {
						log.error("", e);
					}
				}
			} else {
				Connector.this.valid = false;
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			Connector.this.session = session;
			Connector.this.connected = true;
			Connector.this.valid = true;
			if (!Connector.this.needSecureAuth) {
				synchronized (Connector.this.lock) {
					Connector.this.lock.notify();
				}
			}
		}
		
	}

}
