package net.io;

/**
 * A session of HTTP.
 * @author 	fuhuiyuan
 */
public interface IHttpSession {
	
	/**
	 * Get length of a content.
	 * @return	length of a content
	 */
	int getContentLength();
	/**
	 * Get the session id.
	 * @return	session id
	 */
	String getId();
	/**
	 * Get message id of this content.
	 * Indicate this content what to do.
	 * @return	message id
	 */
	int getMessageId();
	/**
	 * Get client connect server's mode.
	 * @return	true means connect aways, false means once
	 */
	boolean isKeepAlive();
	/**
	 * Get sender of this content.
	 * This sender use to send message to server/client
	 * @return	{@link ISender}
	 */
	ISender getSender();

}
