package net.io.protocal.proto;

import java.io.InputStream;

import net.dipatch.ISender;
import net.io.IRequestAndResponseFactory;
import net.io.Request;

public abstract class ProtoIOFactory implements IRequestAndResponseFactory {

	@Override
	public Request createRequest(int receiveMessageId, byte[] datas, ISender sender, String sessionId) {
		return new ProtoRequest(receiveMessageId, datas, sender, sessionId);
	}

	@Override
	public Request createRequest(int receiveMessageId, InputStream is, int contentLength, ISender sender, String sessionId) throws Exception {
		return new ProtoRequest(receiveMessageId, is, contentLength, sender, sessionId);
	}

}
