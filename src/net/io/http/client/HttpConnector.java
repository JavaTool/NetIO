package net.io.http.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.valueOf;

import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

class HttpConnector implements IHttpConnector<HttpResponse> {
	
	private HttpClient httpClient;
	
	private HttpHost httpHost;
	
	private String uri;
	
	private String session;
	
	private IResponseCodeHandler responseCodeHandler;
	
	public HttpConnector() {
		httpClient = HttpClientBuilder.create().build();
		setResponseCodeHandler(code -> {if (code >= 400) throw new Exception("code is : " + code);});
	}
	
	@Override
	public void setUrl(String url) {
		httpHost = HttpHost.create(checkNotNull(url).replaceFirst("http://", "").replaceFirst("https://", "").split("/")[0]);
		this.uri = url;
	}

	@Override
	public String getCookie() {
		return session;
	}

	@Override
	public HttpResponse get(String param, Map<String, Object> head) throws Exception {
		return request(createGetRequest(param), head);
	}

	@Override
	public HttpResponse delete(String param, Map<String, Object> head) throws Exception {
		return request(createDeleteRequest(param), head);
	}

	@Override
	public HttpResponse post(String params, byte[] bytes, Map<String, Object> head) throws Exception {
		HttpEntityEnclosingRequest request = createPostRequest(params);
		makeByteEntity(request, bytes);
		return request(request, head);
	}
	
	private static void makeByteEntity(HttpEntityEnclosingRequest request, byte[] bytes) {
		if (checkNotNull(bytes).length > 0) {
			request.setEntity(new ByteArrayEntity(bytes));
		}
	}

	@Override
	public HttpResponse put(String params, byte[] bytes, Map<String, Object> head) throws Exception {
		HttpEntityEnclosingRequest request = createPutRequest(params);
		makeByteEntity(request, bytes);
		return request(request, head);
	}
	
	private HttpResponse request(HttpRequest request, Map<String, Object> head) throws Exception {
		checkNotNull(head).forEach((k, v) -> request.setHeader(k, valueOf(v)));
		HttpResponse response = httpClient.execute(httpHost, request);
		readCookie(response);
		responseCodeHandler.handleResponseCode(response.getStatusLine().getStatusCode());
		return response;
	}
	
	private void readCookie(HttpResponse response) {
		for (Header header : response.getHeaders("Set-Cookie")) {
			for (String info : header.getValue().split(";")) {
				if (info.toUpperCase().startsWith("JSESSIONID=")) {
					session = info.split("=")[1];
				}
			}
		}
	}
	
	private HttpPost createPostRequest(String param) {
		return new HttpPost(uri + checkNotNull(param));
	}
	
	private HttpGet createGetRequest(String param) {
		return new HttpGet(uri + checkNotNull(param));
	}
	
	private HttpPut createPutRequest(String param) {
		return new HttpPut(uri + checkNotNull(param));
	}
	
	private HttpDelete createDeleteRequest(String param) {
		return new HttpDelete(uri + checkNotNull(param));
	}

	@Override
	public void setResponseCodeHandler(IResponseCodeHandler handler) {
		this.responseCodeHandler = checkNotNull(handler);
	}

}
