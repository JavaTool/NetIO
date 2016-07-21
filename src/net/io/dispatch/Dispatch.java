package net.io.dispatch;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AbstractScheduledService;

/**
 * 默认的消息分配器
 * @author 	fuhuiyuan
 */
public class Dispatch extends AbstractScheduledService implements IDispatch {
	
	protected static final Logger log = LoggerFactory.getLogger(Dispatch.class);
	/**休眠时间*/
	private static int SLEEP_TIME;
	/**消息队列*/
	protected final Queue<IContent> contents;
	/**消息接收器*/
	protected final IContentHandler handler;
	
	public Dispatch(IContentHandler handler) {
		this.handler = handler;
		contents = Queues.newConcurrentLinkedQueue();
	}
	
	/**
	 * 分配工作
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
	 * 获取休眠时间
	 * @return	休眠时间
	 */
	public static int getSLEEP_TIME() {
		return SLEEP_TIME;
	}

	/**
	 * 设置休眠时间
	 * @param 	sLEEP_TIME
	 * 			休眠时间
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
