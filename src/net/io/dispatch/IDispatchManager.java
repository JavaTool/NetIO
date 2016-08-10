package net.io.dispatch;

/**
 * A group of dispatcher.
 * @author 	fuhuiyuan
 */
public interface IDispatchManager extends IDispatch {
	
	/**
	 * Disconnect.
	 * @param 	content
	 * 			
	 */
	void disconnect(IContent content);

}
