package net.io;

import net.io.content.IContentFactory;
import net.io.content.IContentHandler;

import org.apache.commons.configuration.Configuration;

public interface INetServerFactory {
	
	INetServer createNetServer(Configuration configuration, IContentFactory contentFactory, IContentHandler contentHandler);

}
