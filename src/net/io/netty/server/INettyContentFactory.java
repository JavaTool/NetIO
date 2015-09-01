package net.io.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.dipatch.IContent;

public interface INettyContentFactory<T extends IContent> {
	
	T createContent(Channel channel, INettyHttpSession httpSession, ByteBuf content);

}
