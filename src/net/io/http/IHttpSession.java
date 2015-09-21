package net.io.http;

import net.dipatch.ISender;

public interface IHttpSession {
	
	int getContentLength();
	
	String getId();
	
	int getMessageId();
	
	boolean isKeepAlive();
	
	ISender getSender();

}
