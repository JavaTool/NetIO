package net.io.http.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import net.io.http.HttpBackInfo;

class HttpProtoConnector extends DecorateHttpConnector<HttpBackInfo, HttpResponse> {

	public HttpProtoConnector(IHttpConnector<HttpResponse> connector) {
		super(connector);
	}

	@Override
	protected HttpBackInfo from(HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		
		String responseId = "";
		for (String contentType : entity.getContentType().getValue().split(";")) {
			String[] infos = contentType.trim().split("=", -2);
			if (infos[0].toLowerCase().equals("MessageId")) {
				responseId = infos[1];
				break;
			}
		}
		
		try (InputStream is = entity.getContent()) {
			byte[] b = new byte[4096];
			int n;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((n = is.read(b, 0, 4096)) > 0) {
				baos.write(b, 0, n);
			}
			return new HttpBackInfo(baos.toByteArray(), response.getStatusLine().getStatusCode(), responseId);
		}
	}

}
