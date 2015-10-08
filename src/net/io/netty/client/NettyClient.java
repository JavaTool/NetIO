package net.io.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.dipatch.IContentHandler;
import net.io.IContentFactory;
import net.io.INetClient;

public class NettyClient implements INetClient {
	
    protected final EventLoopGroup group;
    
	protected final Bootstrap bootstrap;
	
	protected final NettyClientHandler nettyClientHandler;
	
	protected Channel socketChannel;
	
	public NettyClient(final IContentHandler contentHandler, final IContentFactory contentFactory, int port, String host) throws Exception {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		nettyClientHandler = new NettyClientHandler(contentHandler, contentFactory);
		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.remoteAddress(host, port);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
  		  
	        @Override  
	        protected void initChannel(SocketChannel ch) throws Exception {
	        	ch.pipeline().addLast(nettyClientHandler);
	        }
        
    	});
    	// 发起异步链接操作
    	ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
    	if (channelFuture.isSuccess()) {
    		socketChannel = channelFuture.channel();
    	}
	}
	
	@Override
	public void connect(final byte[] data) {
		ByteBuf clientMessage = Unpooled.buffer(data.length);
		clientMessage.writeInt(data.length);
	    clientMessage.writeBytes(data);
    	socketChannel.writeAndFlush(clientMessage);
	}

	@Override
	public void close() {
		group.shutdownGracefully();
	}

}
