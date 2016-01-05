package net.util;

public class SyncInteger {
	
	private int value;
	
	public SyncInteger(int initValue) {
		value = initValue;
	}
	
	public synchronized int incrementAndGet() {
		value++;
		return value;
	}
	
	public synchronized int decrementAndGet() {
		value--;
		return value;
	}
	
	public synchronized int get() {
		return value;
	}
	
}
