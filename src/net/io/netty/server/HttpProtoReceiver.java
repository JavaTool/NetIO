package net.io.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

import net.dipatch.DispatchManager;
import net.dipatch.IContent;
import net.dipatch.IDispatchManager;
import net.dipatch.Sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProtoReceiver<T extends IContent> {
	
	private final Logger log;
	
	private final IDispatchManager<T> dispatchManager;
	
	private final INettyContentFactory<T> nettyContentFactory;
	
	public HttpProtoReceiver(IDispatchManager<T> dispatchManager, INettyContentFactory<T> nettyContentFactory) {
		this.dispatchManager = dispatchManager;
		this.nettyContentFactory = nettyContentFactory;
		log = LoggerFactory.getLogger(HttpProtoReceiver.class);
	}

	/**
	 * TCP服务器启动函数
	 */
	public void bootStrap(int parentThreadNum, int childThreadNum, String ip, int port, final String path) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(parentThreadNum);
		EventLoopGroup workerGroup = new NioEventLoopGroup(childThreadNum);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 10240).option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000).option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.SO_LINGER, 0).childHandler(new ChannelInitializer<SocketChannel>() {
				
				 @Override 
			 	 protected void initChannel(SocketChannel ch) throws Exception {
					 ChannelPipeline p = ch.pipeline();
					 p.addLast("idleStateHandler", new IdleStateHandler(60, 60, 30)); // 读信道空闲60s,写信道空闲60s,读，写信道空闲30s
					 p.addLast("http_server_codec", new HttpServerCodec()); // http消息转换
			         p.addLast("http_server_handler", new HttpProxyServerHandler<T>(path, dispatchManager, nettyContentFactory)); // 消息处理器 
				 }
	  
			});
			// Start the tcp server.
			ChannelFuture f = b.bind(new InetSocketAddress(ip, port)).sync(); // 启动http服务进程
			log.info("start http server ok at http://{}:{}/", ip, port);
			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			log.info("Shut down all event loops to terminate all threads.");
            bossGroup.shutdownGracefully(); // 关闭服务进程
			workerGroup.shutdownGracefully(); // 关闭服务进程
		}
	}
	
	public static void main(String[] args) {
		try {
			new HttpProtoReceiver<IContent>(new DispatchManager<IContent>(null, 100, 10), new INettyContentFactory<IContent>() {

				@Override
				public IContent createContent(Channel channel, INettyHttpSession httpSession, ByteBuf content) {
					int contentLength = httpSession.getContentLength();
					final byte[] datas = new byte[contentLength < 0 ? 0 : contentLength];
					content.readBytes(datas);
					final String sessionId = httpSession.getId();
					final String messageId = httpSession.getMessageId();
					return new IContent() {
						
						private final Sender sender = new Sender() {
							
							private byte[] datas;
							
							@Override
							public void send(byte[] datas) throws Exception {
								this.datas = datas;
							}

							@Override
							public byte[] getSendDatas() {
								return datas;
							}
							
						};
						
						@Override
						public byte[] getValue() {
							return datas;
						}
						
						@Override
						public String getSessionId() {
							return sessionId;
						}
						
						@Override
						public Sender getSender() {
							return sender;
						}
						
						@Override
						public String getMessageId() {
							return messageId;
						}
						
					};
				}
				
			}).bootStrap(10, 10, "localhost", 8888, "Netty-Http");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
