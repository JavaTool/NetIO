package net.io.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
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
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import net.dipatch.DispatchManager;
import net.dipatch.IContent;
import net.dipatch.IDispatchManager;
import net.dipatch.ISender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpServer implements Runnable {
	
	private final Logger log;
	
	private final IDispatchManager dispatchManager;
	
	private final INettyContentFactory nettyContentFactory;
	
	private final int parentThreadNum;
	
	private final int childThreadNum;
	
	private final int port;
	
	private final String ip;
	
	private ServerBootstrap serverBootstrap;
	
	public NettyHttpServer(IDispatchManager dispatchManager, INettyContentFactory nettyContentFactory, int parentThreadNum, int childThreadNum, int port, String ip) {
		this.dispatchManager = dispatchManager;
		this.nettyContentFactory = nettyContentFactory;
		this.parentThreadNum = parentThreadNum;
		this.childThreadNum = childThreadNum;
		this.port = port;
		this.ip = ip;
		log = LoggerFactory.getLogger(NettyHttpServer.class);
	}

	public void bootStrap() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(parentThreadNum);
		EventLoopGroup workerGroup = new NioEventLoopGroup(childThreadNum);
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 10240).option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000).option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.SO_LINGER, 0).childHandler(new ChannelInitializer<SocketChannel>() {
				
				 @Override 
			 	 protected void initChannel(SocketChannel ch) throws Exception {
					 ChannelPipeline p = ch.pipeline();
					 p.addLast("idleStateHandler", new IdleStateHandler(60, 60, 30)); // 读信道空闲60s,写信道空闲60s,读，写信道空闲30s
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
			bootStrap();
		} catch (Exception e) {
			log.error("[HTTP StartUp Error]", e);
		}
	}

	public void shutdown() {
		// Shut down all event loops to terminate all threads.
		log.info("Shut down all event loops to terminate all threads.");
		serverBootstrap.group().shutdownGracefully();
		serverBootstrap.childGroup().shutdownGracefully();
	}
	
	/**
	 * test
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new NettyHttpServer(new DispatchManager(null, 100, 10), new INettyContentFactory() {

				@Override
				public IContent createContent(final Channel channel, ByteBuf content, INettyHttpSession httpSession) {
					int contentLength = httpSession.getContentLength();
					final byte[] datas = new byte[contentLength < 0 ? 0 : contentLength];
					content.readBytes(datas);
					final String sessionId = httpSession.getId();
					final int messageId = httpSession.getMessageId();
					final String ip = channel.remoteAddress().toString();
					return new IContent() {
						
						private final ISender sender = new ISender() {
							
							@Deprecated
							@Override
							public void send(byte[] datas, String messageId) throws Exception {}

							@Override
							public <X, Y extends X> void setAttribute(String key, Class<X> clz, Y value) {
								AttributeKey<X> attributeKey = AttributeKey.valueOf(key);
								channel.attr(attributeKey).set(value);
							}

							@Override
							public <X> X getAttribute(String key, Class<X> clz) {
								AttributeKey<X> attributeKey = AttributeKey.valueOf(key);
								Attribute<X> attribute = channel.attr(attributeKey);
								return attribute == null ? null : attribute.get();
							}
							
						};
						
						@Override
						public byte[] getDatas() {
							return datas;
						}
						
						@Override
						public String getSessionId() {
							return sessionId;
						}
						
						@Override
						public ISender getSender() {
							return sender;
						}
						
						@Override
						public int getMessageId() {
							return messageId;
						}

						@Override
						public String getIp() {
							return ip;
						}
						
					};
				}

				@Override
				public IContent createContent(Channel channel, ByteBuf content, ISender sender) {
					// TODO Auto-generated method stub
					return null;
				}
				
			}, 10, 10, 8888, "localhost").bootStrap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
