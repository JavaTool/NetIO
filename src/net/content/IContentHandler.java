package net.content;

/**
 * A handler of {@link IContent}
 * @author 	fuhuiyuan
 */
public interface IContentHandler {
	
	/**
	 * Process a content.
	 * @param 	content
	 * 			
	 * @throws 	Exception
	 */
	void handle(IContent content) throws Exception;
	/**
	 * Disconnect a connector.
	 * @param 	sessionId
	 * 			Indicate a id of client who connect server.
	 * @param 	address
	 * 			Server/Client ip
	 */
	void disconnect(String sessionId, String address);

}
