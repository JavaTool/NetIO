package net.content;

import net.io.ISender;

/**
 * A content of net i/o.
 * @author 	fuhuiyuan
 */
public interface IContent {
	
	/**
	 * Get session id of this content.
	 * Indicate a id of client who connect server.
	 * @return	session id
	 */
	String getSessionId();
	/**
	 * Get message id of this content.
	 * Indicate this content what to do.
	 * @return	message id
	 */
	int getMessageId();
	/**
	 * Get byte array datas of message.
	 * @return	byte array datas
	 */
	byte[] getDatas();
	/**
	 * Get sender of this content.
	 * This sender use to send message to server/client
	 * @return	{@link ISender}
	 */
	ISender getSender();

}
