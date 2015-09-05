package net.io.netty.server;

import net.dipatch.ISender;

public interface INettyHttpSession {
	
	int getContentLength();
	
	String getId();
	
	String getMessageId();
	
	boolean isKeepAlive();
	
	ISender getSender();

}
