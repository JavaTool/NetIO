package net.message;

import java.io.IOException;
import java.io.OutputStream;

import net.io.ISender;

public abstract class BaseMessage implements IMessage {
	
	protected int messageId;
	
	protected int status;
	
	protected String sessionId;
	
	protected ISender sender;
	
	protected BaseMessage() {}
	
	protected BaseMessage(int messageId, int status, String sessionId, ISender sender) {
		this.messageId = messageId;
		this.status = status;
		this.sessionId = sessionId;
		this.sender = sender;
	}

	@Override
	public int getMessageId() {
		return messageId;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public ISender getSender() {
		return sender;
	}

	@Override
	public void output(OutputStream os) throws IOException {
		os.write(getByteArray());
	}

}
