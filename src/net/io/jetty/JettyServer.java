package net.io.jetty;

import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer {
	
	protected static final Logger log = LoggerFactory.getLogger(JettyServer.class);
	
	public void start(IJettyConfig jettyConfig) throws Exception {
		DOMConfigurator.configureAndWatch(jettyConfig.getConfigPath());

		QueuedThreadPool threadPool = new QueuedThreadPool(jettyConfig.getThreadPoolSize());
		Server server = new Server(threadPool);
		ServerConnector serverConnector = new ServerConnector(server);
		serverConnector.setPort(jettyConfig.getPort());
		Connector[] connectors = {serverConnector};
		server.setConnectors(connectors);
		
		WebAppContext context = new WebAppContext();
		context.setResourceBase(jettyConfig.getResourceBase());
		context.setDescriptor(jettyConfig.getDescriptor());
		context.setWar(jettyConfig.getWar());
		context.setContextPath(jettyConfig.getContextPath());
		context.setParentLoaderPriority(jettyConfig.getParentLoaderPriority());
		context.setDefaultsDescriptor(jettyConfig.getDefaultsDescriptor());
		context.getSessionHandler().getSessionManager().setMaxInactiveInterval(jettyConfig.getMaxInactiveInterval());

		server.setHandler(context);
		server.start();
		server.join();
		
		log.info("JettyServer start.");
	}

}
