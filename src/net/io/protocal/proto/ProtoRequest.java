package net.io.protocal.proto;

import java.io.BufferedInputStream;
import java.io.InputStream;

import net.dipatch.ISender;
import net.io.Request;

final class ProtoRequest implements Request {
	
	/**最大数据读取次数*/
	protected static final int CONTENT_MAX_READ_TIMES = 5;
	
	/**请求信息的id*/
	protected final int receiveMessageId;
	
	protected final byte[] datas;
	/**数据发送器*/
	protected final ISender sender;
	
	protected final String sessionId;
	
	protected ProtoRequest(int receiveMessageId, byte[] datas, ISender sender, String sessionId) {
		this.receiveMessageId = receiveMessageId;
		this.datas = datas;
		this.sender = sender;
		this.sessionId = sessionId;
	}
	
	protected ProtoRequest(int receiveMessageId, InputStream is, int contentLength, ISender sender, String sessionId) throws Exception {
		this.receiveMessageId = receiveMessageId;
		this.sender = sender;
		this.sessionId = sessionId;
		datas = new byte[contentLength];
		BufferedInputStream bis = new BufferedInputStream(is);
		int readLength = bis.read(datas, 0, contentLength);
		int count = 0;
		while (readLength < contentLength) {
			// 读取次数超过最大设置读取次数时还没有读取全部请求内容，返回错误
			if ((++count) > CONTENT_MAX_READ_TIMES) {
				throw new Exception("Read over times.");
			}
			readLength += bis.read(datas, readLength, contentLength - readLength);
		}
	}

	@Override
	public int getReceiveMessageId() {
		return receiveMessageId;
	}

	@Override
	public byte[] getByteArray() {
		return datas;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public ISender getSender() {
		return sender;
	}

}
