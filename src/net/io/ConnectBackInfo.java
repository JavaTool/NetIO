package net.io;

/**
 * 连接反馈信息
 * @author	fuhuiyuan
 */
public class ConnectBackInfo {

	/**反馈的数据*/
	private final byte[] data;
	/**消息id*/
	private final String messageId;
	
	public ConnectBackInfo(byte[] data, String messageId) {
		this.data = data;
		this.messageId = messageId;
	}

	public byte[] getData() {
		return data;
	}

	public String getMessageId() {
		return messageId;
	}
	
}
