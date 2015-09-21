package net.dipatch;

/**
 * 默认的消息内容
 * @author 	fuhuiyuan
 */
public class Content implements IContent {
	
	/**会话id*/
	private final String sessionId;
	/**消息id*/
	private final int messageId;
	/**数据*/
	private final byte[] datas;
	/**发送器*/
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
