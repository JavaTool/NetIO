package net.io;

import org.apache.commons.configuration.Configuration;

import net.io.dispatch.IContentFactory;
import net.io.dispatch.IContentHandler;

public interface INetServerFactory {
	
	INetServer createNetServer(Configuration configuration, IContentFactory contentFactory, IContentHandler contentHandler);

}
