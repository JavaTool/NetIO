package net.io;

public interface IHttpSession {
	
	int getContentLength();
	
	String getId();
	
	int getMessageId();
	
	boolean isKeepAlive();
	
	ISender getSender();

}
