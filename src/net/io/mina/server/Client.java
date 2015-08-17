package net.io.mina.server;

import net.io.mina.Packet;
import net.io.mina.server.session.ClientSession;

public interface Client {
	
	void setSession(ClientSession session);
	
	ClientSession getSession();
	
	void setDisconnecting();
	
	Packet createSessionKeyPacket();
	
}
