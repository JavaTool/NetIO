package net.dipatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatchManager<T extends IContent> implements IDispatchManager<T> {
	
	protected final Map<String, IDispatchManager<T>> dispatchs;
	
	protected final IContentHandler handler;
	
	protected final ScheduledExecutorService executorService;
	
	protected final int sleepTime;
	
	public DispatchManager(IContentHandler handler, int sleepTime, int corePoolSize) {
		this.handler = handler;
		this.sleepTime = sleepTime;
		dispatchs = new ConcurrentHashMap<String, IDispatchManager<T>>();
		executorService = new ScheduledThreadPoolExecutor(corePoolSize);
	}

	@Override
	public void addDispatch(T content) {
		IDispatchManager<T> dispatch = fetch(content.getSessionId());
		dispatch.addDispatch(content);
	}

	@Override
	public void fireDispatch(T content) {
		IDispatchManager<T> dispatch = fetch(content.getSessionId());
		dispatch.fireDispatch(content);
	}
	
	protected synchronized IDispatchManager<T> fetch(String key) {
		IDispatchManager<T> dispatch = dispatchs.get(key);
		if (dispatch == null) {
			Dispatch<T> dis = new Dispatch<T>(handler);
			executorService.scheduleWithFixedDelay(dis, sleepTime, sleepTime, TimeUnit.MILLISECONDS);
			dispatchs.put(key, dis);
			return dis;
		} else {
			return dispatch;
		}
	}
	
	public static void main(String[] args) {
		DispatchManager<IContent> dispatchManager = new DispatchManager<IContent>(null, 100, 10);
		dispatchManager.fetch("111");
	}

}
