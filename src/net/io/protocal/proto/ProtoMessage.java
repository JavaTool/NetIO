package net.io.protocal.proto;

import net.dipatch.ISender;
import net.io.BaseMessage;

public class ProtoMessage extends BaseMessage {
	
	protected byte[] datas;
	
	public ProtoMessage(int messageId, int status, String sessionId, ISender sender, byte[] datas) {
		super(messageId, status, sessionId, sender);
		mergeFrom(datas);
	}
	
	protected ProtoMessage() {
		super();
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
