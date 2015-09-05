package net.io.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.dipatch.Content;
import net.dipatch.IContent;
import net.dipatch.ISender;
import net.io.netty.server.INettyContentFactory;
import net.io.netty.server.INettyHttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientContentFactory implements INettyContentFactory {
	
	protected static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("SESSION_ID");
	
	protected static final Logger log = LoggerFactory.getLogger(NettyClientContentFactory.class);

	@Deprecated
	@Override
	public IContent createContent(Channel channel, ByteBuf content, INettyHttpSession httpSession) {
		return null;
	}

	@Override
	public IContent createContent(Channel channel, ByteBuf content, ISender sender) {
	    byte[] data = new byte[content.readableBytes()];
	    content.readBytes(data);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		try {
			int messageId = dis.readInt();
			int messageLength = dis.readInt();
			byte[] datas = new byte[messageLength];
			dis.read(datas);
			String ip = channel.remoteAddress().toString();
			Attribute<String> attSession = channel.attr(SESSION_ID);
			String sessionId = attSession == null ? "" : attSession.get();
			return new Content(sessionId, messageId, ip, datas, sender);
		} catch (IOException e) {
			log.error("", e);
			return null;
		}
	}

}
