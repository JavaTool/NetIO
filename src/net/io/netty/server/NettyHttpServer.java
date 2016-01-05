package net.io.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import net.content.IContentFactory;
import net.dipatch.IDispatchManager;
import net.io.INetServer;

public class NettyHttpServer implements INetServer, Runnable {
	
	private final Logger log;
	
	private final IDispatchManager dispatchManager;
	
	private final IContentFactory nettyContentFactory;
	
	private final int parentThreadNum;
	
	private final int childThreadNum;
	
	private final int port;
	
	private final int soBacklog;
	
	private final String ip;
	
	private final long readerIdleTime;
	
	private final long writerIdleTime;
	
	private final long allIdleTime;
	
	private ServerBootstrap serverBootstrap;
	
	public NettyHttpServer(NettyHttpServerConfig config) {
		dispatchManager = config.getDispatchManager();
		nettyContentFactory = config.getNettyContentFactory();
		parentThreadNum = config.getParentThreadNum();
		childThreadNum = config.getChildThreadNum();
		soBacklog = config.getSoBacklog();
		port = config.getPort();
		ip = config.getIp();
		readerIdleTime = config.getReaderIdleTime();
		writerIdleTime = config.getWriterIdleTime();
		allIdleTime = config.getAllIdleTime();
		log = LoggerFactory.getLogger(NettyHttpServer.class);
	}

	@Override
	public void bind() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(parentThreadNum);
		EventLoopGroup workerGroup = new NioEventLoopGroup(childThreadNum);
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, soBacklog).option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_KEEPALIVE, true).childHandler(new ChannelInitializer<SocketChannel>() {
				
				 @Override 
			 	 protected void initChannel(SocketChannel ch) throws Exception {
					 ChannelPipeline p = ch.pipeline();
					 p.addLast("idleStateHandler", new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS)); // 读信道空闲,写信道空闲,读，写信道空闲
					 p.addLast("HttpRequestDecoder", new HttpRequestDecoder()); // http消息转换
					 p.addLast("http_server_codec", new HttpServerCodec()); // http消息转换
			         p.addLast("http_server_handler", createChannelHandler()); // 消息处理器 
				 }
	  
			});
			// Start the tcp server.
			ChannelFuture f = serverBootstrap.bind(new InetSocketAddress(ip, port)); // 启动http服务进程
			log.info("start http server ok at http://{}:{}/ [readerIdleTime, writerIdleTime, allIdleTime, soBacklog, parentThreadNum, childThreadNum][{}, {}, {}, {}, {}, {}]", 
					ip, port, readerIdleTime, writerIdleTime, allIdleTime, soBacklog, parentThreadNum, childThreadNum);
			// Wait until the server socket is closed.
			f.channel().closeFuture().await();
		} finally {
			shutdown(); // 关闭服务进程
		}
	}
	
	protected SimpleChannelInboundHandler<Object> createChannelHandler() {
		return new NettyHttpHandler(dispatchManager, nettyContentFactory);
	}

	@Override
	public void run() {
		try {
			bind();
		} catch (Exception e) {
			log.error("[HTTP StartUp Error]", e);
		}
	}

	@Override
	public void shutdown() {
		// Shut down all event loops to terminate all threads.
		log.info("Shut down all event loops to terminate all threads.");
		serverBootstrap.group().shutdownGracefully();
		serverBootstrap.childGroup().shutdownGracefully();
	}

	@Override
	public int getPort() {
		return port;
	}

}
