package net.io;

public interface INetServer {
	
	void bind() throws Exception;
	
	void shutdown();
	
	int getPort();

}
