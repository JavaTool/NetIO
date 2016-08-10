package net.io.dispatch;

import net.io.ISender;

/**
 * The default implement of {@link IContent}.
 * @author 	hyfu
 */
public class Content implements IContent {
	
	private final String sessionId;
	
	private final int messageId;
	
	private final byte[] datas;
	
	private final ISender sender;
	
	public Content(String sessionId, int messageId, byte[] datas, ISender sender) {
		this.sessionId = sessionId;
		this.messageId = messageId;
		this.datas = datas;
		this.sender = sender;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public int getMessageId() {
		return messageId;
	}

	@Override
	public byte[] getDatas() {
		return datas;
	}

	@Override
	public ISender getSender() {
		return sender;
	}

}
