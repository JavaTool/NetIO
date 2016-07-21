package net.io.http.client;

import static com.alibaba.fastjson.JSON.parseObject;

import com.alibaba.fastjson.JSONObject;

class HttpJsonConnector extends DecorateHttpConnector<JSONObject, byte[]> {
	
	public HttpJsonConnector(IHttpConnector<byte[]> connector) {
		super(connector);
	}
	
	@Override
	protected JSONObject from(byte[] bytes) {
		return bytesToJson(bytes);
	}
	
	static JSONObject bytesToJson(byte[] bytes) {
		return parseObject(new String(bytes));
	}

}
