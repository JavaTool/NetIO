package net.io.message;

import java.io.IOException;
import java.io.OutputStream;

import net.dipatch.ISender;

public interface IMessage {
	
	byte[] NULL_SEND_DATAS = new byte[0];
	
	int getMessageId();
	
	int getStatus();
	
	byte[] getByteArray();
	
	String getSessionId();
	
	ISender getSender();
	
	void mergeFrom(byte[] data) throws Exception;
	
	void output(OutputStream os) throws IOException;

}
