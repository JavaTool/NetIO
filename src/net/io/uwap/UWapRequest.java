package net.io.uwap;

import net.io.Request;

public class UWapRequest implements Request {
	
	/**客户端地址*/
	protected final String ip;
	/**请求信息的id*/
	protected final String receiveMessageId;
	
	protected UWapRequest(String ip, String receiveMessageId) {
		this.ip = ip;
		this.receiveMessageId = receiveMessageId;
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public String getReceiveMessageId() {
		return receiveMessageId;
	}

	@Override
	public byte[] getByteArray() {
		return NULL_REQUEST;
	}

}
