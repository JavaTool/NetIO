package net.dipatch;

import net.content.IContent;

/**
 * 分配器管理器
 * @author 	fuhuiyuan
 */
public interface IDispatchManager extends IDispatch {
	
	/**
	 * 断开
	 * @param 	content
	 * 			断开消息
	 */
	void disconnect(IContent content);

}
