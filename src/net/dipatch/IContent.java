package net.dipatch;

public interface IContent {
	
	String getSessionId();
	
	int getMessageId();
	
	byte[] getValue();
	
	void send(byte[] datas);

}
