package net.util;

public class IOUtil {
	
	public static final byte TRUE = 1;
	
	public static final byte FALSE = 0;
    
    public static String ip2str(int ip) {
    	return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
    }

}
