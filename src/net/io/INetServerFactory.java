package net.io;

import org.apache.commons.configuration.Configuration;

import net.content.IContentFactory;
import net.content.IContentHandler;

public interface INetServerFactory {
	
	INetServer createNetServer(Configuration configuration, IContentFactory contentFactory, IContentHandler contentHandler);

}
