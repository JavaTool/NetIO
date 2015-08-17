package net.io.mina.server;

public interface IOLog {
	
	void info(String paramString);
	
	void error(Exception e);
	
	void debug(String paramString, Throwable paramThrowable);

}
