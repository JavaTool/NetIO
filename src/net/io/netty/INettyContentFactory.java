package net.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.dipatch.IContent;
import net.dipatch.ISender;
import net.io.netty.server.INettyHttpSession;

public interface INettyContentFactory {
	
	IContent createContent(Channel channel, ByteBuf content, INettyHttpSession httpSession);
	
	IContent createContent(Channel channel, ByteBuf content, ISender sender);

}
