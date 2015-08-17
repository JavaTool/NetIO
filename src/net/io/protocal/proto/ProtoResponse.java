package net.io.protocal.proto;

import java.io.IOException;
import java.io.OutputStream;

import net.io.Response;

public abstract class ProtoResponse implements Response {
	
	protected int sendMessageId;
	
	protected byte[] sendDatas;
	
	protected int status;
	
	protected int sessionId;

	@Override
	public void mergeFrom(byte[] data) throws Exception {
		sendDatas = data;
	}

	@Override
	public int getSendMessageId() {
		return sendMessageId;
	}

	@Override
	public void setSendMessageId(int sendMessageId) {
		this.sendMessageId = sendMessageId;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void output(OutputStream os) throws IOException {
		os.write(sendDatas);
	}

}
