package net.io.content;

import net.io.ISender;
import net.io.http.IHttpSession;

public interface IContentFactory {
	
	String SESSION_ID = "SESSION_ID";
	
	IContent createContent(byte[] data, IHttpSession httpSession);
	
	IContent createContent(byte[] data, ISender sender);

}
