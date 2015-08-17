package net.io;

import java.io.IOException;
import java.io.OutputStream;

public class RedirectResponse implements Response {
	
	private String url;
	
	private Response response;

	public RedirectResponse(Response response) {
		this.response = response;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setError(int errorCode, String errorMsg) {
		response.setError(errorCode, errorMsg);
	}

	@Override
	public boolean hasError() {
		return response.hasError();
	}

	@Override
	public void mergeFrom(byte[] data) throws Exception {
		response.mergeFrom(data);
	}

	@Override
	public int getSendMessageId() {
		return response.getSendMessageId();
	}

	@Override
	public void setSendMessageId(int sendMessageId) {
		response.setSendMessageId(sendMessageId);
	}

	@Override
	public int getStatus() {
		return response.getStatus();
	}

	@Override
	public void output(OutputStream os) throws IOException {
		response.output(os);
	}

}
