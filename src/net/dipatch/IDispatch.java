package net.dipatch;

import net.content.IContent;

/**
 * 消息分配器
 * @author 	fuhuiyuan
 */
public interface IDispatch {
	
	/**
	 * 添加消息
	 * @param 	content
	 * 			消息内容
	 */
	void addDispatch(IContent content);
	/**
	 * 立即分配消息
	 * @param 	content
	 * 			消息内容
	 */
	void fireDispatch(IContent content);

}
