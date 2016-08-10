package net.io.dispatch;

/**
 * The content handler.
 * @author	hyfu
 */
public interface IContentHandler {
	
	/**
	 * Hanle content.
	 * @param 	content
	 * 			
	 * @throws 	Exception
	 */
	void handle(IContent content) throws Exception;
	/**
	 * Disconnect a connection.
	 * @param 	sessionId
	 * 			The connection's sessionId
	 * @param 	address
	 * 			The connection's address.
	 */
	void disconnect(String sessionId, String address);

}
