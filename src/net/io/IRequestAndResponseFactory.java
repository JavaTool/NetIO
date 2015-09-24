package net.io;

import java.io.InputStream;

import net.dipatch.ISender;

public interface IRequestAndResponseFactory {
	
	Request createRequest(int receiveMessageId, byte[] datas, ISender sender, String sessionId);
	
	Request createRequest(int receiveMessageId, InputStream is, int contentLength, ISender sender, String sessionId) throws Exception;
	
	Response createResponse();

}
