package net.io.mina.server;

import net.io.mina.server.session.ClientSession;
import net.io.util.TrustIp;

public interface SessionManager {
	
	void add(ClientSession session);
	
	void remove(ClientSession session);
	
	TrustIp getTrustIp();

}
