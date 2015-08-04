package net.io.proto;

import java.io.InputStream;

import net.io.IOFactory;
import net.io.Request;
import net.io.Response;

public class ProtoIOFactory implements IOFactory {

	@Override
	public Request createRequest(String ip, int receiveMessageId, byte[] datas) {
		return new ProtoRequest(ip, receiveMessageId, datas);
	}

	@Override
	public Request createRequest(String ip, int receiveMessageId, InputStream is, int contentLength) throws Exception {
		return new ProtoRequest(ip, receiveMessageId, is, contentLength);
	}

	@Override
	public Response createResponse() {
		return new ProtoResponse();
	}

}
