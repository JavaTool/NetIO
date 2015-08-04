package net.io.proto;

import java.io.BufferedInputStream;
import java.io.InputStream;

import net.io.Request;

class ProtoRequest implements Request {
	
	/**最大数据读取次数*/
	protected static final int CONTENT_MAX_READ_TIMES = 5;
	
	/**客户端地址*/
	protected final String ip;
	/**请求信息的id*/
	protected final int receiveMessageId;
	
	protected final byte[] datas;
	
	protected ProtoRequest(String ip, int receiveMessageId, byte[] datas) {
		this.ip = ip;
		this.receiveMessageId = receiveMessageId;
		this.datas = datas;
	}
	
	protected ProtoRequest(String ip, int receiveMessageId, InputStream is, int contentLength) throws Exception {
		this.ip = ip;
		this.receiveMessageId = receiveMessageId;
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
	public String getIp() {
		return ip;
	}

	@Override
	public int getReceiveMessageId() {
		return receiveMessageId;
	}

	@Override
	public byte[] getByteArray() {
		return datas;
	}

}
