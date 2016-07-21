package net.io.dispatch;

import java.io.DataOutputStream;

import net.io.ISender;
import net.io.anthenticate.IDataAnthenticate;

/**
 * A factory of {@link IContent} creator.
 * @author 	hyfu
 */
public interface IContentFactory {
	
	/**
	 * session id name.
	 */
	String SESSION_ID = "SESSION_ID";
	
	/**
	 * To create a content from full params.
	 * @param 	sessionId
	 * 			Indicate a id of client who connect server.
	 * @param 	messageId
	 * 			Indicate this content what to do.
	 * @param 	datas
	 * 			Byte array datas of message.
	 * @param 	sender
	 * 			Use to send message to server/client
	 * @return	{@link IContent}
	 */
	IContent createContent(String sessionId, int messageId, byte[] datas, ISender sender);
	/**
	 * To create a content from byte array and sender.
	 * @param 	data
	 * 			Include sessionId, messageId and byte array datas of message.
	 * @param 	sender
	 * 			Use to send message to server/client
	 * @return	{@link IContent}
	 */
	IContent createContent(byte[] data, ISender sender);
	
	IDataAnthenticate<byte[], DataOutputStream> getDataAnthenticate();

}
