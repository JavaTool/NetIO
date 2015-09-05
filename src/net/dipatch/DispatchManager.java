package net.dipatch;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

/**
 * 默认的分配管理器
 * @author 	fuhuiyuan
 */
public class DispatchManager implements IDispatchManager {
	
	/**分配器集合*/
	protected final Map<String, IDispatch> dispatchs;
	/**消息接收器*/
	protected final IContentHandler handler;
	/**任务执行服务*/
	protected final ScheduledExecutorService executorService;
	/**休眠时间*/
	protected final int sleepTime;
	
	public DispatchManager(IContentHandler handler, int sleepTime, int corePoolSize) {
		this.handler = handler;
		this.sleepTime = sleepTime;
		Dispatch.setSLEEP_TIME(sleepTime);
		dispatchs = Maps.newConcurrentMap();
		executorService = new ScheduledThreadPoolExecutor(corePoolSize);
	}

	@Override
	public void addDispatch(IContent content) {
		IDispatch dispatch = fetch(content);
		dispatch.addDispatch(content);
	}

	@Override
	public void fireDispatch(IContent content) {
		IDispatch dispatch = fetch(content);
		dispatch.fireDispatch(content);
	}
	
	/**
	 * 获取一个分配器
	 * @param 	content
	 * 			消息内容
	 * @return	分配器
	 */
	protected synchronized IDispatch fetch(IContent content) {
		String key = content.getSessionId();
		IDispatch dispatch = dispatchs.get(key);
		if (dispatch == null) {
			Dispatch dis = new Dispatch(handler);
			executorService.scheduleWithFixedDelay(dis, sleepTime, sleepTime, TimeUnit.MILLISECONDS);
			dispatchs.put(key, dis);
			return dis;
		} else {
			return dispatch;
		}
	}

	@Override
	public void disconnect(IContent content) {
		String key = content.getSessionId();
		dispatchs.remove(key);
	}

}
