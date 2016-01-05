package net.io;

/**
 * A server of net connect.
 * @author 	fuhuiyuan
 */
public interface INetServer {
	
	/**
	 * Binding an ip and port, start this server.
	 * @throws 	Exception
	 */
	void bind() throws Exception;
	/**
	 * Stop server.
	 */
	void shutdown();
	/**
	 * The port of this server.
	 * @return	port of this server
	 */
	int getPort();

}
