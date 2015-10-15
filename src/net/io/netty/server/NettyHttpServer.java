package net.io.netty.server;

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
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import net.dipatch.IDispatchManager;
import net.io.IContentFactory;
import net.io.INetServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpServer implements INetServer, Runnable {
	
	private final Logger log;
	
	private final IDispatchManager dispatchManager;
	
	private final IContentFactory nettyContentFactory;
	
	private final int parentThreadNum;
	
	private final int childThreadNum;
	
	private final int port;
	
	private final String ip;
	
	private ServerBootstrap serverBootstrap;
	
	public NettyHttpServer(IDispatchManager dispatchManager, IContentFactory nettyContentFactory, int parentThreadNum, int childThreadNum, int port, String ip) {
		this.dispatchManager = dispatchManager;
		this.nettyContentFactory = nettyContentFactory;
		this.parentThreadNum = parentThreadNum;
		this.childThreadNum = childThreadNum;
		this.port = port;
		this.ip = ip;
		log = LoggerFactory.getLogger(NettyHttpServer.class);
	}

	@Override
	public void bind() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(parentThreadNum);
		EventLoopGroup workerGroup = new NioEventLoopGroup(childThreadNum);
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 10240).option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.SO_LINGER, 0).childHandler(new ChannelInitializer<SocketChannel>() {
				
				 @Override 
			 	 protected void initChannel(SocketChannel ch) throws Exception {
					 ChannelPipeline p = ch.pipeline();
					 p.addLast("idleStateHandler", new IdleStateHandler(600, 600, 300, TimeUnit.SECONDS)); // 读信道空闲600s,写信道空闲600s,读，写信道空闲300s
					 p.addLast("http_server_codec", new HttpServerCodec()); // http消息转换
			         p.addLast("http_server_handler", createChannelHandler()); // 消息处理器 
				 }
	  
			});
			// Start the tcp server.
			ChannelFuture f = serverBootstrap.bind(new InetSocketAddress(ip, port)); // 启动http服务进程
			log.info("start http server ok at http://{}:{}/", ip, port);
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
