package net.io.mina.server.service;

import net.io.mina.server.PacketHandler;
import net.io.mina.server.SessionManager;

import org.apache.commons.configuration.Configuration;

public class AdminClientSessionService extends DirectClientSessionService {
	
	public AdminClientSessionService(Configuration config, PacketHandler handler, SessionManager sessionManager){
		super(config, handler, sessionManager);
	}
	
}
