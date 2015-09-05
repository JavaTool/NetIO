package net.dipatch;

/**
 * 数据发送器
 * @author 	fuhuiyuan
 */
public interface ISender {
	
	/**
	 * 发送数据
	 * @param 	datas
	 * 			数据
	 * @throws 	Exception
	 */
	void send(byte[] datas, String messageId) throws Exception;
	
	<X, Y extends X> void setAttribute(String key, Class<X> clz, Y value);
	
	<X> X getAttribute(String key, Class<X> clz);

}
