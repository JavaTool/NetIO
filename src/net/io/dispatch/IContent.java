package net.io.dispatch;

import net.io.ISender;

/**
 * 消息内容
 * @author 	fuhuiyuan
 */
public interface IContent {
	
	/**
	 * 获取会话id
	 * @return	会话id
	 */
	String getSessionId();
	/**
	 * 获取消息id
	 * @return	消息id
	 */
	int getMessageId();
	/**
	 * 获取消息数据
	 * @return	消息数据
	 */
	byte[] getDatas();
	/**
	 * 获取发送器
	 * @return	发送器
	 */
	ISender getSender();

}
