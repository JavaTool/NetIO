package net.io.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.dipatch.IContent;
import net.dipatch.IContentHandler;
import net.dipatch.ISender;
import net.io.netty.INettyContentFactory;
import net.io.netty.Packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP处理器
 * @author 	fuhuiyuan
 */
public class NettyTcpHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	protected static final AttributeKey<ISender> SENDER_KEY = AttributeKey.valueOf("SENDER_KEY");
	
	protected static final Logger log = LoggerFactory.getLogger(NettyTcpHandler.class);
	/**消息处理器*/
	private final IContentHandler contentHandler;
	/**消息工厂*/
	private final INettyContentFactory contentFactory;
	
	public NettyTcpHandler(IContentHandler contentHandler, INettyContentFactory contentFactory) {
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
//		log.info("[Coming Active]IP:{}", ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("[Coming Out]IP:{}", ctx.channel().remoteAddress());
		ctx.channel().close();
		ctx.close();
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
		}
		
		IContent content = contentFactory.createContent(channel, msg, sender);
		if (content != null) {
			contentHandler.handle(content);
		}
	}

	/**
	 * 发送二进制数据
	 * @param 	channel
	 * 			数据
	 * @param 	message
	 * 			数据
	 * @return	
	 * @throws 	IOException
	 */
	public static ByteBuf sendPacket(Channel channel, Packet packet) throws IOException {
		if (channel != null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bout);
			dos.writeInt(packet.getMessageId());
			byte[] value = packet.getValue();
			dos.writeInt(value.length);
//			dos.write(EncryptUtil.encrypt(resultMessage, resultMessage.length, EncryptUtil.PASSWORD));
			dos.write(value);
			byte[] bytes = bout.toByteArray();
			ByteBuf result = Unpooled.copiedBuffer(bytes);
			channel.writeAndFlush(result);
			return result;
		} else {
			return null;
		}
	}

}
