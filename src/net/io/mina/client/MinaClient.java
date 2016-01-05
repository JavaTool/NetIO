package net.io.mina.client;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import net.content.IContentFactory;
import net.content.IContentHandler;

public abstract class MinaClient {
	
	protected static final Logger log = Logger.getLogger(MinaClient.class);
	
	protected SocketAddress address;
	
	protected SocketConnector connector;
	
	protected SocketConnectorConfig config;

	protected int receiveBufferSize = 32767, sendBufferSize = 32767;
	
	protected ExecutorService executor;
	
	protected ConnectFuture connectFuture;
	
	protected MinaClientHandler minaHandler;
	
	protected final IContentHandler contentHandler;
	
	protected final IContentFactory contentFactory;
	
	public MinaClient(String ip, int port, IContentHandler contentHandler, IContentFactory contentFactory) {
		this.address = new InetSocketAddress(ip, port);
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
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
		minaHandler = createMinaClientHandler();
		init();
	}
	
	protected abstract void init();
	
	protected MinaClientHandler createMinaClientHandler() {
		return new MinaClientHandler(contentHandler, contentFactory);
	}
	
	public void connect() throws ConnectException {
//		if ((connected) || (valid)) {
//			throw new IllegalStateException("connection is connected");
//		} else {
			connectFuture = connector.connect(address, minaHandler, config);
//			synchronized (lock) {
//				try {
//					lock.wait(10000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (!valid) {
//					if (session != null) {
//						session.close();
//					}
//					connected = false;
//					throw new ConnectException("connect [" + address + "] error");
//				}
//			}
//		}
	}
	
//	public boolean isValid() {
//		return valid;
//	}
	
	public void close() throws Exception {
//		setNeedRetry(false);
		minaHandler.close();
		executor.shutdown();
		config.getFilterChain().clear();
	}
	
	public SocketAddress getRemoteAddress() {
		return address;
	}
	
	public void send(Object object) {
		minaHandler.send(object);
	}
	
//	public void setNeedRetry(boolean needRetry) {
//		this.needRetry = needRetry;
//	}
	
//	public boolean isNeedRetry() {
//		return needRetry;
//	}
	
	public boolean isConnected() {
		return minaHandler.isConnected();
	}

}
