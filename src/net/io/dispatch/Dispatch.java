package net.io.dispatch;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * The default implement of {@link IDispatch}.<br>
 * Once in a while process all contents.
 * @author 	hyfu
 */
public class Dispatch extends AbstractScheduledService implements IDispatch {
	
	protected static final Logger log = LoggerFactory.getLogger(Dispatch.class);
	/** Work interval. */
	private static int SLEEP_TIME;
	
	protected final Queue<IContent> contents;
	
	protected final IContentHandler handler;
	
	public Dispatch(IContentHandler handler) {
		this.handler = handler;
		contents = Queues.newConcurrentLinkedQueue();
	}
	
	/**
	 * Process all contents.
	 */
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
		try {
			handler.handle(content);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * Get work interval.
	 * @return	Work interval.
	 */
	public static int getSLEEP_TIME() {
		return SLEEP_TIME;
	}

	/**
	 * Set work interval.
	 * @param 	sLEEP_TIME
	 * 			Work interval.
	 */
	public static void setSLEEP_TIME(int sLEEP_TIME) {
		SLEEP_TIME = sLEEP_TIME;
	}

	@Override
	protected void runOneIteration() throws Exception {
		work();
	}

	@Override
	protected Scheduler scheduler() {
		return Scheduler.newFixedDelaySchedule(getSLEEP_TIME(), getSLEEP_TIME(), TimeUnit.MILLISECONDS);
	}

}
