package net.io.http.client;

import java.util.Map;

public abstract class DecorateHttpConnector<T, S> implements IHttpConnector<T> {
	
	private final IHttpConnector<S> connector;
	
	public DecorateHttpConnector(IHttpConnector<S> connector) {
		this.connector = connector;
	}

	@Override
	public void setUrl(String url) {
		connector.setUrl(url);
	}

	@Override
	public void setResponseCodeHandler(IResponseCodeHandler handler) {
		connector.setResponseCodeHandler(handler);
	}

	@Override
	public String getCookie() {
		return connector.getCookie();
	}

	@Override
	public T get(String params, Map<String, Object> head) throws Exception {
		return from(connector.get(params, head));
	}

	@Override
	public T delete(String params, Map<String, Object> head) throws Exception {
		return from(connector.delete(params, head));
	}

	@Override
	public T post(String params, byte[] bytes, Map<String, Object> head) throws Exception {
		return from(connector.post(params, bytes, head));
	}

	@Override
	public T put(String params, byte[] bytes, Map<String, Object> head) throws Exception {
		return from(connector.put(params, bytes, head));
	}
	
	protected abstract T from(S s) throws Exception;

}
