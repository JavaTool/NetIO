package net.io;

import net.dipatch.ISender;

public interface Request {
	
	int getReceiveMessageId();
	
	byte[] getByteArray();
	
	String getSessionId();
	
	ISender getSender();

}
