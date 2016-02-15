package net.io.netty.server.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.io.ISender;
import net.io.anthenticate.IDataAnthenticate;

public class NettyTcpSender implements ISender {
	
	protected final Channel channel;
	
	protected final IDataAnthenticate<byte[], DataOutputStream> dataAnthenticate;
	
	public NettyTcpSender(Channel channel, IDataAnthenticate<byte[], DataOutputStream> dataAnthenticate) {
		this.channel = channel;
		this.dataAnthenticate = dataAnthenticate;
	}
	
	private int getAnthenticateLength() {
		return dataAnthenticate == null ? 0 : dataAnthenticate.getAnthenticateLength();
	}

	@Override
	public void send(byte[] datas, int messageId) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bout);
		dos.writeInt(datas.length + 8 + getAnthenticateLength());
		if (dataAnthenticate != null) {
			dataAnthenticate.write(dos);
		}
		dos.writeInt(messageId);
		dos.writeInt(datas.length);
//		dos.write(EncryptUtil.encrypt(resultMessage, resultMessage.length, EncryptUtil.PASSWORD));
		dos.write(datas);
		byte[] bytes = bout.toByteArray();
		ByteBuf result = Unpooled.copiedBuffer(bytes);
		channel.writeAndFlush(result);
	}

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

	@Override
	public String getIp() {
		return channel.remoteAddress().toString();
	}

}
