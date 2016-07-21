package net.io.http.client;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

class HttpInputStreamConnector extends DecorateHttpConnector<InputStream, HttpResponse> {

	public HttpInputStreamConnector(IHttpConnector<HttpResponse> connector) {
		super(connector);
	}

	@Override
	protected InputStream from(HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		try (InputStream is = entity.getContent()) {
			return is;
		}
	}

}
