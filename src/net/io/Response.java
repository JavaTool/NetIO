package net.io;

import java.io.IOException;
import java.io.OutputStream;

public interface Response extends ErrorInfo {
	
	byte[] NULL_SEND_DATAS = new byte[0];
	
	void mergeFrom(byte[] data) throws Exception;
	
	int getSendMessageId();
	
	void setSendMessageId(int sendMessageId);
	
	int getStatus();
	
	void output(OutputStream os) throws IOException;

}
