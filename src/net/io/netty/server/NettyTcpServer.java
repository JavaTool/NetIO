package net.io.netty.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.content.IContentFactory;
import net.content.IContentHandler;
import net.io.INetServer;

/**
 * TCP协议接收器
 * @author 	fuhuiyuan
 */
public class NettyTcpServer implements INetServer {
	
	/**端口*/
	private final int port;
	
	private final String ip;
	/**消息接收器*/
	private final IContentHandler contentHandler;
	
	private final IContentFactory contentFactory;
	
	private ServerBootstrap serverBootstrap;

	public NettyTcpServer(String ip, int port, IContentHandler contentHandler, IContentFactory contentFactory) {
		this.ip = ip;
		this.port = port;
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}

	@Override
	public void bind() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO));
			// 注册TCP处理流水线
			serverBootstrap.childHandler(new NettyTcpInitializer(contentHandler, contentFactory));
			serverBootstrap.bind(new InetSocketAddress(ip, port)).sync().channel().closeFuture().sync();
		} finally {
			shutdown();
		}
	}

	@Override
	public void shutdown() {
		serverBootstrap.group().shutdownGracefully();
		serverBootstrap.childGroup().shutdownGracefully();
	}

	@Override
	public int getPort() {
		return port;
	}

}
