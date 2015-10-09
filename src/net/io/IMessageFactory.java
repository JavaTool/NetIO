package net.io;

import java.io.InputStream;

import net.dipatch.ISender;

public interface IMessageFactory {
	
	IMessage createMessage(int messageId, int status, String sessionId, ISender sender, byte[] datas) throws Exception;
	
	IMessage createMessage(int messageId, int status, String sessionId, ISender sender, InputStream is, int contentLength) throws Exception;

}
