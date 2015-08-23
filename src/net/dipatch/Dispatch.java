package net.dipatch;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatch implements IDispatchManager, Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(Dispatch.class);
	
	private static int SLEEP_TIME;
	
	protected final Queue<IContent> contents;
	
	protected final IContentHandler handler;
	
	public Dispatch(IContentHandler handler) {
		this.handler = handler;
		contents = new ConcurrentLinkedQueue<IContent>();
	}
	
	@Override
	public void run() {
		try {
			work();
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	protected void work() {
		while (contents.size() > 0) {
			fireDispatch(contents.poll());
		}
	}

	@Override
	public void addDispatch(IContent content) {
		contents.add(content);
	}

	@Override
	public void fireDispatch(IContent content) {
		long time = System.currentTimeMillis();
		handler.hanle(content);
		time = System.currentTimeMillis() - time;
		
		if (time > getSLEEP_TIME()) {
			log.warn("Too long time {} ms at {}.", time, content.getMessageId());
		}
	}

	public static int getSLEEP_TIME() {
		return SLEEP_TIME;
	}

	public static void setSLEEP_TIME(int sLEEP_TIME) {
		SLEEP_TIME = sLEEP_TIME;
	}

}
