package net.io.mina.server.tcp;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import net.io.INetServer;
import net.io.dispatch.IContentFactory;
import net.io.dispatch.IContentHandler;
import net.io.mina.server.NullDecoder;
import net.io.mina.server.NullEncoder;

public class MinaTcpServer implements INetServer {
	
	protected final int port;
	
	protected final String ip;
	
	protected final IContentHandler contentHandler;
	
	protected final IContentFactory contentFactory;

	protected SocketAcceptor acceptor;

	public MinaTcpServer(String ip, int port, IContentHandler contentHandler, IContentFactory contentFactory) {
		this.ip = ip;
		this.port = port;
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}

	@Override
	public void bind() throws Exception {
		acceptor = new SocketAcceptor();
		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		cfg.getFilterChain().addLast("codec", createProtocolCodecFilter());
		acceptor.bind(new InetSocketAddress(ip, port),  createIoHandlerAdapter(), cfg);
	}

	@Override
	public void shutdown() {
		acceptor.unbind(new InetSocketAddress(ip, port));
	}

	@Override
	public int getPort() {
		return port;
	}
	
	protected ProtocolCodecFilter createProtocolCodecFilter() {
		return new ProtocolCodecFilter(NullEncoder.class, NullDecoder.class);
	}
	
	protected IoHandlerAdapter createIoHandlerAdapter() {
		return new MinaTcpHandler(contentHandler, contentFactory);
	}

}
