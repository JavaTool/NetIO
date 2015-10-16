package net.io;

public interface IMessageSender {
	
	void send(IMessage message);
	
	String getSessionId();

}
