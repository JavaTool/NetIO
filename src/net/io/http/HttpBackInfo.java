package net.io.http;

import net.io.ConnectBackInfo;

/**
 * HTTP反馈信息
 * @author	fuhuiyuan
 */
public class HttpBackInfo extends ConnectBackInfo {
	
	/**服务器状态*/
	private final int status;
	
	public HttpBackInfo(byte[] data, int status, String messageId) {
		super(data, messageId);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
