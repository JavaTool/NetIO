package net.io;

/**
 * 错误信息接收者
 * @author	fuhuiyuan
 */
public interface ErrorInfo {
	
	/**
	 * 设置错误内容
	 * @param 	errorCode
	 * 			错误码
	 * @param 	errorMsg
	 * 			错误消息
	 */
	void setError(int errorCode, String errorMsg);
	/**
	 * 获取是否有错误消息
	 * @return	是否有错误消息
	 */
	boolean hasError();

}
