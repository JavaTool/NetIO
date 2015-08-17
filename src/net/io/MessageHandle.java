package net.io;

import io.netty.channel.Channel;

/**
 * 消息接收器
 * @author 	fuhuiyuan
 */
public interface MessageHandle {
	
	/**
	 * 接收消息
	 * @param 	receiveDatas
	 * 			接收的数据
	 * @param 	ip
	 * 			客户端地址
	 * @param 	receiveMessageId
	 * 			接收的消息id
	 * @param 	sessionId
	 * 			会话id
	 * @param 	channel
	 * 			Netty客户端频道
	 * @return	响应对象
	 * @throws 	Exception
	 */
	Response handle(byte[] receiveDatas, String ip, int receiveMessageId, String sessionId, Channel channel) throws Exception;

}
