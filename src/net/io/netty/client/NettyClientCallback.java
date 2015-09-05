package net.io.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface NettyClientCallback {
	
	void callback(Channel channel, ByteBuf buf) throws Exception;

}
