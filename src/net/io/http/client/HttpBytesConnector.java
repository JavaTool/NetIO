package net.io.http.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

class HttpBytesConnector extends DecorateHttpConnector<byte[], HttpResponse> {
	
	public HttpBytesConnector(IHttpConnector<HttpResponse> connector) {
		super(connector);
	}
	
	@Override
	protected byte[] from(HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		try (InputStream is = entity.getContent()) {
			byte[] b = new byte[4096];
			int n;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((n = is.read(b, 0, 4096)) > 0) {
				baos.write(b, 0, n);
			}
			return baos.toByteArray();
		}
	}

}
