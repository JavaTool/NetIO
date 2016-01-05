package net.io.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.content.IContent;
import net.content.IContentFactory;
import net.content.IContentHandler;
import net.io.netty.server.tcp.NettyTcpSender;

@Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	
	private final IContentHandler contentHandler;
	
	private final IContentFactory contentFactory;

	public NettyClientHandler(IContentHandler contentHandler, IContentFactory contentFactory) {
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}
	  
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
	    ByteBuf buf = (ByteBuf) msg;
	    Channel channel = ctx.channel();
	    byte[] data = new byte[buf.readableBytes()];
	    buf.readBytes(data);
	    IContent content = contentFactory.createContent(data, new NettyTcpSender(channel));
	    contentHandler.handle(content);
	}
	  
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    ctx.close();
	}
	
}
