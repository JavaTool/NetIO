package net.message;

public interface IMessageSender {
	
	void send(IMessage message);
	
	String getSessionId();

}
