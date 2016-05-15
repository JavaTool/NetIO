package net.io.netty.server;

import net.content.IContentFactory;
import net.dipatch.IDispatchManager;

public class DefaultNettyServerConfig implements INettyServerConfig {
	
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

	@Override
	public IDispatchManager getDispatchManager() {
		return dispatchManager;
	}

	public void setDispatchManager(IDispatchManager dispatchManager) {
		this.dispatchManager = dispatchManager;
	}

	@Override
	public IContentFactory getNettyContentFactory() {
		return nettyContentFactory;
	}

	public void setNettyContentFactory(IContentFactory nettyContentFactory) {
		this.nettyContentFactory = nettyContentFactory;
	}

	@Override
	public int getParentThreadNum() {
		return parentThreadNum;
	}

	public void setParentThreadNum(int parentThreadNum) {
		this.parentThreadNum = parentThreadNum;
	}

	@Override
	public int getChildThreadNum() {
		return childThreadNum;
	}

	public void setChildThreadNum(int childThreadNum) {
		this.childThreadNum = childThreadNum;
	}

	@Override
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public long getReaderIdleTime() {
		return readerIdleTime;
	}

	public void setReaderIdleTime(long readerIdleTime) {
		this.readerIdleTime = readerIdleTime;
	}

	@Override
	public long getWriterIdleTime() {
		return writerIdleTime;
	}

	public void setWriterIdleTime(long writerIdleTime) {
		this.writerIdleTime = writerIdleTime;
	}

	@Override
	public long getAllIdleTime() {
		return allIdleTime;
	}

	public void setAllIdleTime(long allIdleTime) {
		this.allIdleTime = allIdleTime;
	}

	@Override
	public int getSoBacklog() {
		return soBacklog;
	}

	public void setSoBacklog(int soBacklog) {
		this.soBacklog = soBacklog;
	}

}
