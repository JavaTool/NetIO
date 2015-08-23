package net.dipatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DispatchManager implements IDispatchManager {
	
	protected final Map<String, IDispatchManager> dispatchs;
	
	protected final IContentHandler handler;
	
	protected final ScheduledExecutorService executorService;
	
	protected final int sleepTime;
	
	public DispatchManager(IContentHandler handler, int sleepTime, int corePoolSize) {
		this.handler = handler;
		this.sleepTime = sleepTime;
		dispatchs = new ConcurrentHashMap<String, IDispatchManager>();
		executorService = new ScheduledThreadPoolExecutor(corePoolSize);
	}

	@Override
	public void addDispatch(IContent content) {
		IDispatchManager dispatch = fetch(content.getSessionId());
		dispatch.addDispatch(content);
	}

	@Override
	public void fireDispatch(IContent content) {
		IDispatchManager dispatch = fetch(content.getSessionId());
		dispatch.fireDispatch(content);
	}
	
	protected synchronized IDispatchManager fetch(String key) {
		IDispatchManager dispatch = dispatchs.get(key);
		if (dispatch == null) {
			Dispatch dis = new Dispatch(handler);
			executorService.scheduleWithFixedDelay(dis, sleepTime, sleepTime, TimeUnit.MILLISECONDS);
			dispatchs.put(key, dis);
			return dis;
		} else {
			return dispatch;
		}
	}
	
	public static void main(String[] args) {
		DispatchManager dispatchManager = new DispatchManager(null, 100, 10);
		dispatchManager.fetch("111");
	}

}
