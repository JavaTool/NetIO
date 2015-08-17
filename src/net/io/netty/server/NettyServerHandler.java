package net.io.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;

import net.io.MessageHandle;
import net.io.netty.Packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty处理器
 * @author 	fuhuiyuan
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	/**
	 * Channel组
	 */
	protected static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	protected static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
	
	/**消息处理器*/
	private final MessageHandle messageHandle;
	
	public NettyServerHandler(MessageHandle messageHandle) {
		this.messageHandle = messageHandle;
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
		log.info("[Coming Out Error]IP:{}", ctx.channel().remoteAddress());
		ctx.channel().close();
		ctx.close();
		log.info(cause.getMessage());
	}

	/**
	 * 处理接受的报文数据
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		Channel channel = ctx.channel();
		SocketAddress address = channel.remoteAddress();
		log.info("[Coming in]IP:{}MSG:{}", address);
		byte[] messageBytes = new byte[msg.capacity()];
		msg.readBytes(messageBytes);

		Packet packet = readPacket(messageBytes);
		messageHandle.handle(packet.getValue(), address.toString().split(":")[0].replace("/", ""), packet.getMessageId(), "", channel);
	}
	
	private Packet readPacket(byte[] content) throws IOException {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(content));
		Packet packet = new Packet();
		packet.setMessageId(data.readInt());
		int messageLength = data.readInt();
		byte[] value = new byte[messageLength];
		data.read(value);
		packet.setValue(value);
		return packet;
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
