package net.io.mina.server.service;

import net.io.INetServer;
import net.io.mina.server.IOLog;
import net.io.mina.server.session.ClientSession;

public interface ClientSessionService extends INetServer {
	
	void addClientSession(ClientSession session);
	
	void removeClientSession(ClientSession session);
	
	String getAddress();
	
	int getPort();
	
	String getEncryptionKey();
	
	int getEncryptionLen();
	
	void setIOLog(IOLog log);
	
}
