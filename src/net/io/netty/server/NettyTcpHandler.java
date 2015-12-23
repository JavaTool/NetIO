package net.io.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.io.ISender;
import net.io.content.IContent;
import net.io.content.IContentFactory;
import net.io.content.IContentHandler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP处理器
 * @author 	fuhuiyuan
 */
public class NettyTcpHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	protected static final AttributeKey<ISender> SENDER_KEY = AttributeKey.valueOf("SENDER_KEY");
	
	protected static final AttributeKey<String> SESSSION_ID_KEY = AttributeKey.valueOf(IContentFactory.SESSION_ID);
	
	protected static final Logger log = LoggerFactory.getLogger(NettyTcpHandler.class);
	/**消息处理器*/
	private final IContentHandler contentHandler;
	/**消息工厂*/
	private final IContentFactory contentFactory;
	
	public NettyTcpHandler(IContentHandler contentHandler, IContentFactory contentFactory) {
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
//		log.info("[Coming Active]IP:{}", ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String address = ctx.channel().remoteAddress().toString();
		String sessionId = ctx.channel().attr(SESSSION_ID_KEY).get();
		log.info("[Coming Out]IP:{}", address);
		ctx.channel().close();
		ctx.close();
		if (sessionId != null) {
			contentHandler.disconnect(sessionId, address);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.info("[Coming Out Error]IP:{} ; ERROR:{}", ctx.channel().remoteAddress(), cause.getMessage());
		ctx.channel().close();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		Channel channel = ctx.channel();
		Attribute<ISender> attribute = channel.attr(SENDER_KEY);
		ISender sender = attribute.get();
		if (sender == null) {
			sender = new NettyTcpSender(channel);
			attribute.set(sender);
			Attribute<String> session = channel.attr(SESSSION_ID_KEY);
			session.set(UUID.randomUUID().toString());
		}

	    byte[] data = new byte[msg.readableBytes()];
	    msg.readBytes(data);
		IContent content = contentFactory.createContent(data, sender);
		if (content != null) {
			contentHandler.handle(content);
		}
	}

}
