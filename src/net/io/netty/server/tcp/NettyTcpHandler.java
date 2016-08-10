package net.io.netty.server.tcp;

import java.io.DataOutputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.io.ISender;
import net.io.anthenticate.IDataAnthenticate;
import net.io.dispatch.Content;
import net.io.dispatch.IContent;
import net.io.dispatch.IContentFactory;
import net.io.dispatch.IDispatchManager;
import net.io.netty.NettyTcpSender;
import net.io.netty.server.http.NettyHttpSender;

/**
 * TCP处理器
 * @author 	fuhuiyuan
 */
public class NettyTcpHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	protected static final AttributeKey<ISender> SENDER_KEY = AttributeKey.valueOf("SENDER_KEY");
	
	protected static final AttributeKey<String> SESSSION_ID_KEY = AttributeKey.valueOf(IContentFactory.SESSION_ID);
	
	protected static final Logger log = LoggerFactory.getLogger(NettyTcpHandler.class);
	/**消息处理器*/
	protected final IDispatchManager dispatchManager;
	/**消息工厂*/
	protected final IContentFactory contentFactory;
	
	protected final int anthencateLength;
	
	public NettyTcpHandler(IDispatchManager dispatchManager, IContentFactory contentFactory) {
		this.dispatchManager = dispatchManager;
		this.contentFactory = contentFactory;
		IDataAnthenticate<byte[], DataOutputStream> dataAnthenticate = contentFactory.getDataAnthenticate();
		anthencateLength =  dataAnthenticate == null ? 0 : dataAnthenticate.getAnthenticateLength();
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
//		log.info("[Coming Active]IP:{}", ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		String address = channel.remoteAddress().toString();
		String sessionId = channel.attr(SESSSION_ID_KEY).get();
		log.info("[Coming Out]IP:{}", address);
		channel.close();
		ctx.close();
		if (sessionId != null) {
			dispatchManager.disconnect(new Content(sessionId, 0, null, new NettyHttpSender(true, sessionId, channel)));
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
		if (check(msg)) {
			Channel channel = ctx.channel();
			Attribute<ISender> attribute = channel.attr(SENDER_KEY);
			ISender sender = attribute.get();
			if (sender == null) {
				sender = new NettyTcpSender(channel, contentFactory.getDataAnthenticate());
				attribute.set(sender);
				Attribute<String> session = channel.attr(SESSSION_ID_KEY);
				session.set(UUID.randomUUID().toString());
			}
	
		    byte[] data = new byte[msg.readableBytes()];
		    msg.readBytes(data);
			IContent content = contentFactory.createContent(data, sender);
			if (content != null) {
				dispatchManager.addDispatch(content);
			}
		}
	}
	
	private boolean check(ByteBuf msg) {
		if (anthencateLength > 0) {
			byte[] data = new byte[anthencateLength];
			msg.readBytes(data);
			return contentFactory.getDataAnthenticate().read(data);
		} else {
			return true;
		}
	}

}
