package net.io.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.dipatch.Sender;

public class NettySender implements Sender {
	
	protected final Channel channel;
	
	public NettySender(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void send(byte[] datas) throws Exception {
		ByteBuf result = Unpooled.copiedBuffer(datas);
		channel.writeAndFlush(result);
	}

}
