package net.io.netty.server.http;

import net.io.dispatch.IContentFactory;
import net.io.dispatch.IDispatchManager;

public class NettyHttpServerConfig {
	
	private IDispatchManager dispatchManager;
	
	private IContentFactory nettyContentFactory;
	
	private int parentThreadNum;
	
	private int childThreadNum;
	
	private int port;
	
	private int soBacklog;
	
	private String ip;
	
	private long readerIdleTime;
	
	private long writerIdleTime;
	
	private long allIdleTime;

	public IDispatchManager getDispatchManager() {
		return dispatchManager;
	}

	public void setDispatchManager(IDispatchManager dispatchManager) {
		this.dispatchManager = dispatchManager;
	}

	public IContentFactory getNettyContentFactory() {
		return nettyContentFactory;
	}

	public void setNettyContentFactory(IContentFactory nettyContentFactory) {
		this.nettyContentFactory = nettyContentFactory;
	}

	public int getParentThreadNum() {
		return parentThreadNum;
	}

	public void setParentThreadNum(int parentThreadNum) {
		this.parentThreadNum = parentThreadNum;
	}

	public int getChildThreadNum() {
		return childThreadNum;
	}

	public void setChildThreadNum(int childThreadNum) {
		this.childThreadNum = childThreadNum;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getReaderIdleTime() {
		return readerIdleTime;
	}

	public void setReaderIdleTime(long readerIdleTime) {
		this.readerIdleTime = readerIdleTime;
	}

	public long getWriterIdleTime() {
		return writerIdleTime;
	}

	public void setWriterIdleTime(long writerIdleTime) {
		this.writerIdleTime = writerIdleTime;
	}

	public long getAllIdleTime() {
		return allIdleTime;
	}

	public void setAllIdleTime(long allIdleTime) {
		this.allIdleTime = allIdleTime;
	}

	public int getSoBacklog() {
		return soBacklog;
	}

	public void setSoBacklog(int soBacklog) {
		this.soBacklog = soBacklog;
	}

}
