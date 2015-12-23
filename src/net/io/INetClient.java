package net.io;

public interface INetClient {
	
	void connect(byte[] data);
	
	void close() throws Exception;

}
