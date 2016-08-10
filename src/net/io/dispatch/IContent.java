package net.io.dispatch;

import net.io.ISender;

/**
 * The content of a message.
 * @author 	hyfu
 */
public interface IContent {
	
	/**
	 * Get the session id.
	 * @return	session id
	 */
	String getSessionId();
	/**
	 * Get the message id.
	 * @return	message id
	 */
	int getMessageId();
	/**
	 * Get the message datas.
	 * @return	message datas
	 */
	byte[] getDatas();
	/**
	 * Get the sender which can send response.
	 * @return	The sender which can send response.
	 */
	ISender getSender();

}
