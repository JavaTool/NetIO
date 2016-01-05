package net.io.mina.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import net.content.IContentFactory;
import net.content.IContentHandler;
import net.io.INetClient;

public class MinaClient implements INetClient {

	protected static final Logger log = Logger.getLogger(MinaClient.class);
	
	protected SocketConnector connector;
	
	protected SocketConnectorConfig config;

	protected int receiveBufferSize = 32767, sendBufferSize = 32767;
	
	protected ExecutorService executor;
	
	protected ConnectFuture connectFuture;
	
	protected MinaClientHandler minaHandler;
	
	protected final IContentHandler contentHandler;
	
	protected final IContentFactory contentFactory;
	
	public MinaClient(IContentHandler contentHandler, IContentFactory contentFactory) {
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
		initConnector();
	}
	
	protected void initConnector() {
		executor = Executors.newCachedThreadPool();
		connector = new SocketConnector(1, executor);
		config = new SocketConnectorConfig();
		config.setThreadModel(ThreadModel.MANUAL);
		config.getSessionConfig().setTcpNoDelay(true);
		config.getSessionConfig().setSendBufferSize(sendBufferSize);
		config.getSessionConfig().setReceiveBufferSize(receiveBufferSize);
		minaHandler = createMinaClientHandler();
	}
	
	protected MinaClientHandler createMinaClientHandler() {
		return new MinaClientHandler(contentHandler, contentFactory);
	}

	@Override
	public void close() throws Exception {
		minaHandler.close();
		executor.shutdown();
		config.getFilterChain().clear();
	}
	
	@Override
	public void send(byte[] data) {
		ByteBuffer buff = ByteBuffer.allocate(data.length);
		buff.putInt(data.length);
		buff.put(data);
		minaHandler.send(buff);
	}

	@Override
	public void connect(String ip, int port) throws Exception {
		connectFuture = connector.connect(new InetSocketAddress(ip, port), minaHandler, config);
	}

}
