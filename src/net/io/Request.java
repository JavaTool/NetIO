package net.io;

public interface Request {
	
	byte[] NULL_REQUEST = new byte[0];
	
	String getIp();
	
	String getReceiveMessageId();
	
	byte[] getByteArray();

}
