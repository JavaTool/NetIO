package net.io;

public interface IOFactory {
	
	Request createRequest(String ip, String receiveMessageId);
	
	Response createResponse(Request request);

}
