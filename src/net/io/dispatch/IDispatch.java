package net.io.dispatch;

/**
 * Dispatcher of content.
 * @author 	hyfu
 */
public interface IDispatch {
	
	/**
	 * Add a content wait to process.
	 * @param 	content
	 * 			消息内容
	 */
	void addDispatch(IContent content);
	/**
	 * Add a content and process at once.
	 * @param 	content
	 * 			
	 */
	void fireDispatch(IContent content);

}
