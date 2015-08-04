package net.io;

import java.io.InputStream;

public interface IOFactory {
	
	Request createRequest(String ip, int receiveMessageId, byte[] datas);
	
	Request createRequest(String ip, int receiveMessageId, InputStream is, int contentLength) throws Exception;
	
	Response createResponse();

}
