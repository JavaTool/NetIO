package net.io.proto;

import java.io.OutputStream;

import net.io.Response;

public class ProtoResponse implements Response {
	
	protected int sendMessageId;
	
	protected byte[] sendDatas;
	
	protected int status;
	
	protected int sessionId;

	@Override
	public void setError(int errorCode, String errorMsg) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mergeFrom(byte[] data) throws Exception {
		// TODO Auto-generated method stub

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
	public void output(OutputStream os) throws Exception {
		os.write(sendDatas);
	}

}
