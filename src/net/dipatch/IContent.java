package net.dipatch;

public interface IContent {
	
	String getSessionId();
	
	String getMessageId();
	
	byte[] getValue();
	
	Sender getSender();

}
