package net.io;

/**
 * A client of net connect.
 * @author 	fuhuiyuan
 */
public interface INetClient {
	
	void send(byte[] data);
	
	void close() throws Exception;

}
