package net.io.netty.server;

public interface INettyHttpSession {
	
	int getContentLength();
	
	String getId();
	
	String getMessageId();
	
	boolean isKeepAlive();

}
