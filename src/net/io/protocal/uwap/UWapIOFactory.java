package net.io.protocal.uwap;

import java.io.InputStream;

import net.io.IOFactory;
import net.io.Request;
import net.io.Response;

public class UWapIOFactory implements IOFactory {

	@Override
	public Request createRequest(String ip, int receiveMessageId, byte[] datas) {
		return new UWapRequest(ip, receiveMessageId, datas);
	}

	@Override
	public Request createRequest(String ip, int receiveMessageId, InputStream is, int contentLength) throws Exception {
		return new UWapRequest(ip, receiveMessageId, is, contentLength);
	}

	@Override
	public Response createResponse() {
		return new UWapResponse();
	}

}
