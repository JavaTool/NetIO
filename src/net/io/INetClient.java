package net.io;

/**
 * A client of net connect.
 * @author 	fuhuiyuan
 */
public interface INetClient {
	
	/**
	 * Connect a server.
	 * @throws Exception
	 */
	void connect(String ip, int port) throws Exception;
	/**
	 * Send data of byte array.
	 * @param 	data
	 * 			byte array
	 */
	void send(byte[] data);
	/**
	 * Close the connect.
	 * @throws 	Exception
	 */
	void close() throws Exception;

}
