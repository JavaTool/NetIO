package net.message;

import net.io.ISender;

public class ByteArrayMessage extends BaseMessage {
	
	protected byte[] datas;
	
	public ByteArrayMessage(int messageId, int status, String sessionId, ISender sender, byte[] datas) {
		super(messageId, status, sessionId, sender);
		mergeFrom(datas);
	}
	
	protected ByteArrayMessage() {
		super();
		mergeFrom(NULL_SEND_DATAS);
	}

	@Override
	public byte[] getByteArray() {
		return datas;
	}

	@Override
	public void mergeFrom(byte[] datas) {
		this.datas = datas;
	}

}
