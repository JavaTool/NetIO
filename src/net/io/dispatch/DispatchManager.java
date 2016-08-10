package net.io.dispatch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.google.common.util.concurrent.Service;

/**
 * The default implement of {@link IDispatchManager}.<br>
 * @author 	hyfu
 */
public class DispatchManager implements IDispatchManager {
	
	protected final Map<String, IDispatch> dispatchs;
	
	protected final IContentHandler handler;
	
	protected final ScheduledExecutorService executorService;
	
	protected final List<Service> serviceList;
	
	public DispatchManager(IContentHandler handler, int sleepTime, int corePoolSize, List<Service> serviceList) {
		this.handler = handler;
		this.serviceList = serviceList;
		Dispatch.setSLEEP_TIME(sleepTime);
		dispatchs = new ConcurrentHashMap<String, IDispatch>();
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
	 * Get a dispatch which can process the content.
	 * @param 	content
	 * 			
	 * @return	A dispatch which can process the content.
	 */
	protected synchronized IDispatch fetch(IContent content) {
		String key = content.getSessionId();
		IDispatch dispatch = dispatchs.get(key);
		if (dispatch == null) {
			Dispatch dis = new Dispatch(handler);
			serviceList.add(dis);
			dispatchs.put(key, dis);
			dis.startAsync();
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
