package net.io.jetty;

public interface IJettyConfig {
	
	default String getConfigPath() {
		return "config/configuration.cml";
	}
	
	default int getThreadPoolSize() {
		return 20;
	}
	
	default int getPort() {
		return 8080;
	}
	
	default String getResourceBase() {
		return "./" + getWar();
	}
	
	default String getDescriptor() {
		return getResourceBase() + "/WEB-INF/web.xml";
	}
	
	default String getWar() {
		return "WebContent";
	}
	
	String getContextPath();
	
	default boolean getParentLoaderPriority() {
		return true;
	}
	
	default String getDefaultsDescriptor() {
		return "config/webdefault.xml";
	}
	
	default int getMaxInactiveInterval() {
		return 86400000;
	}

}
