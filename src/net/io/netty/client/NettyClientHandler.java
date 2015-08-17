package net.io.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	
	private final NettyClientCallback callback;

	public NettyClientHandler(NettyClientCallback callback) {
		this.callback = callback;
	}
	  
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
	    ByteBuf buf = (ByteBuf) msg;
	    byte[] data = new byte[buf.readableBytes()];
	    buf.readBytes(data);
	    Channel channel = ctx.channel();
	    callback.callback(data, channel.remoteAddress().toString(), channel);
	}
	  
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    ctx.close();
	}
	
}
