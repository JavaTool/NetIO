package net.dipatch;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Queues;

import net.content.IContent;
import net.content.IContentHandler;

public class BlockDispatch implements IDispatch, Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BlockDispatch.class);
	
	protected BlockingQueue<IContent> queue;
	/**消息接收器*/
	protected final IContentHandler handler;
	
	public BlockDispatch(IContentHandler handler) {
		this.handler = handler;
		queue = Queues.newLinkedBlockingQueue();
	}

	@Override
	public void addDispatch(IContent content) {
		queue.add(content);
	}

	@Override
	public void fireDispatch(IContent content) {
		execute(content);
	}

	@Override
	public void run() {
		while (true) {
			try {
				IContent content = queue.take();
				execute(content);
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}
	
	private void execute(IContent content) {
		try {
			handler.handle(content);
		} catch (Exception e) {
			log.error("", e);
		}
	}

}
