package net.io.protocal.proto;

import java.io.InputStream;

import net.io.IRequestAndResponseFactory;
import net.io.Request;

public abstract class ProtoIOFactory implements IRequestAndResponseFactory {

	@Override
	public Request createRequest(String ip, int receiveMessageId, byte[] datas) {
		return new ProtoRequest(ip, receiveMessageId, datas);
	}

	@Override
	public Request createRequest(String ip, int receiveMessageId, InputStream is, int contentLength) throws Exception {
		return new ProtoRequest(ip, receiveMessageId, is, contentLength);
	}

}
