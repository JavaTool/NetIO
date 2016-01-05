package net.content;

import net.io.IHttpSession;
import net.io.ISender;

public interface IContentFactory {
	
	String SESSION_ID = "SESSION_ID";
	
	IContent createContent(byte[] data, IHttpSession httpSession);
	
	IContent createContent(byte[] data, ISender sender);

}
