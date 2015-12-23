package net.io.http;

import net.io.ISender;

public interface IHttpSession {
	
	int getContentLength();
	
	String getId();
	
	int getMessageId();
	
	boolean isKeepAlive();
	
	ISender getSender();

}
