package net.dipatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchManager implements IDispatchManager {
	
	protected final Map<String, IDispatchManager> dispatchs;
	
	protected final IContentHandler handler;
	
	public DispatchManager(IContentHandler handler) {
		this.handler = handler;
		dispatchs = new ConcurrentHashMap<String, IDispatchManager>();
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
			dispatch = new Dispatch(handler);
			dispatchs.put(key, dispatch);
		}
		return dispatch;
	}

}
