package net.io.protocal.uwap;

import java.io.InputStream;

import net.dipatch.ISender;
import net.io.IRequestAndResponseFactory;
import net.io.Request;
import net.io.Response;

public class UWapIOFactory implements IRequestAndResponseFactory {

	@Override
	public Request createRequest(int receiveMessageId, byte[] datas, ISender sender, String sessionId) {
		return new UWapRequest(receiveMessageId, datas, sender, sessionId);
	}

	@Override
	public Request createRequest(int receiveMessageId, InputStream is, int contentLength, ISender sender, String sessionId) throws Exception {
		return new UWapRequest(receiveMessageId, is, contentLength, sender, sessionId);
	}

	@Override
	public Response createResponse() {
		return new UWapResponse();
	}

}
