package net.io.message;

public interface IMessageSender {
	
	void send(IMessage message);
	
	String getSessionId();

}
