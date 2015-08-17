package net.io.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.io.MessageHandle;

/**
 * Netty初始化管理器
 * @author 	fuhuiyuan
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
	
	private final MessageHandle messageHandle;
	
	public NettyServerInitializer(MessageHandle messageHandle) {
		this.messageHandle = messageHandle;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		// 粘包处理
		pipeline.addLast("Decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
		pipeline.addLast("Handler", new NettyServerHandler(messageHandle));
	}

}
