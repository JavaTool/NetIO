package net.io.uwap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;

import net.io.Request;
import net.io.Response;

class UWapResponse implements Response {
	
	protected static final byte[] HEAD = {'U', 'A'};
	
	protected static final byte[] PROTOCOL = {'D', 'A'};
	
	protected int sendMessageId;
	
	protected byte[] sendDatas;
	
	protected int status;
	
	protected int sessionId;
	
	protected UWapError error;
	
	protected final Request request;
	
	protected UWapResponse(Request request) {
		this.request = request;
	}

	@Override
	public void mergeFrom(byte[] data) throws Exception {
		setSendDatas(data);
	}

	protected void setSendDatas(byte[] sendDatas) {
		this.sendDatas = sendDatas;
	}

	@Override
	public int getSendMessageId() {
		return sendMessageId;
	}

	@Override
	public void setSendMessageId(int sendMessageId) {
		this.sendMessageId = sendMessageId;
	}

	protected void build() throws Exception {
		if (hasError()) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutput output = new DataOutputStream(stream);
			output.writeInt(error.errorCode);
			output.writeUTF(error.errorMsg);
			setSendDatas(stream.toByteArray());
			stream.close();
		} else {
			setSendDatas(NULL_SEND_DATAS);
		}
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setError(int errorCode, String errorMsg) {
		error = new UWapError();
		error.errorCode = errorCode;
		error.errorMsg = errorMsg;
	}

	@Override
	public boolean hasError() {
		return error != null;
	}
	
	protected static class UWapError {
		
		public String errorMsg;
		
		public int errorCode;
		
	}

	@Override
	public void output(OutputStream os) throws Exception {
		build();
		DataOutput out = new DataOutputStream(os);
		int dataLen = sendDatas.length + HEAD.length + 8;
		out.write(PROTOCOL);
		out.writeInt(dataLen + 10);
		out.writeInt(sessionId);
		out.write(HEAD);
		out.writeInt(dataLen);
		out.writeInt(getSendMessageId());
		out.write(sendDatas);
		os.flush();
	}

}
