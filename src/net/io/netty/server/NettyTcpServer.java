package net.io.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.dipatch.IContentHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP协议接收器
 * @author 	fuhuiyuan
 */
public class NettyTcpServer implements Runnable {
	
	private final Logger log;
	/**端口*/
	private final int port;
	/**消息接收器*/
	private final IContentHandler contentHandler;
	
	private final INettyContentFactory contentFactory;
	
	private ServerBootstrap serverBootstrap;

	public NettyTcpServer(int port, IContentHandler contentHandler, INettyContentFactory contentFactory) {
		this.port = port;
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
		log = LoggerFactory.getLogger(NettyTcpServer.class);
	}

	/**
	 * TCP服务器启动函数
	 */
	private void bootStrap() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO));
			// 注册TCP处理流水线
			serverBootstrap.childHandler(new NettyTcpInitializer(contentHandler, contentFactory));
			// 开始监听
			log.info("TCP启动成功");
			serverBootstrap.bind(port).sync().channel().closeFuture().sync();
		} finally {
			shutdown();
		}
	}

	@Override
	public void run() {
		try {
			bootStrap();
		} catch (Exception e) {
			log.error("[TCP StartUp Error]", e);
		}
	}

	public void shutdown() {
		serverBootstrap.group().shutdownGracefully();
		serverBootstrap.childGroup().shutdownGracefully();
	}

}
