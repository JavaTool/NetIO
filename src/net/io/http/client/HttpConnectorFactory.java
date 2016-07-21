package net.io.http.client;

import java.io.InputStream;

import org.apache.http.HttpResponse;

import com.alibaba.fastjson.JSONObject;

import net.io.http.HttpBackInfo;

public class HttpConnectorFactory {
	
	private HttpConnectorFactory() {}
	
	public static IHttpConnector<HttpResponse> createHttpResponse() {
		return new HttpConnector();
	}
	
	public static IHttpConnector<byte[]> createBytes() {
		return new HttpBytesConnector(createHttpResponse());
	}
	
	public static IHttpConnector<JSONObject> createJSONObject() {
		return new HttpJsonConnector(createBytes());
	}
	
	public static IHttpConnector<IJsonAndBytes> createJsonAndBytes() {
		return new HttpJsonAndBytesConnector(createBytes());
	}
	
	public static IHttpConnector<HttpBackInfo> createHttpBackInfo() {
		return new HttpProtoConnector(createHttpResponse());
	}
	
	public static IHttpConnector<InputStream> createInputStream() {
		return new HttpInputStreamConnector(createHttpResponse());
	}

}
