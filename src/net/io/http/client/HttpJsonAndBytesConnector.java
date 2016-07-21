package net.io.http.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.alibaba.fastjson.JSONObject;

class HttpJsonAndBytesConnector extends DecorateHttpConnector<IJsonAndBytes, byte[]> {
	
	public HttpJsonAndBytesConnector(IHttpConnector<byte[]> connector) {
		super(connector);
	}
	
	@Override
	protected IJsonAndBytes from(byte[] bytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		int size = dis.readInt();
		byte[] jsonBytes = new byte[size];
		dis.read(jsonBytes);
		JSONObject jsonObject = HttpJsonConnector.bytesToJson(jsonBytes);
		byte[] datas = new byte[dis.available()];
		dis.read(datas);
		return new JsonAndBytes(jsonObject, datas);
	}
	
	private static class JsonAndBytes implements IJsonAndBytes {
		
		private final JSONObject jsonObject;
		
		private final byte[] bytes;
		
		public JsonAndBytes(JSONObject jsonObject, byte[] bytes) {
			this.jsonObject = jsonObject;
			this.bytes = bytes;
		}

		@Override
		public JSONObject getJson() {
			return jsonObject;
		}

		@Override
		public byte[] getBytes() {
			return bytes;
		}
		
	}

}
