package net.io.netty.client;

import io.netty.channel.Channel;

public interface NettyClientCallback {
	
	void callback(byte[] data, String ip, Channel channel);

}
