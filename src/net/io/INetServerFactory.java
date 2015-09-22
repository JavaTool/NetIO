package net.io;

import net.dipatch.IContentHandler;

import org.apache.commons.configuration.Configuration;

public interface INetServerFactory {
	
	INetServer createNetServer(Configuration configuration, IContentFactory contentFactory, IContentHandler contentHandler);

}
