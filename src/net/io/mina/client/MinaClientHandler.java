package net.io.mina.client;

import org.apache.log4j.Logger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import net.io.dispatch.IContent;
import net.io.dispatch.IContentFactory;
import net.io.dispatch.IContentHandler;
import net.io.mina.server.tcp.MinaTcpSender;

public class MinaClientHandler extends IoHandlerAdapter {
	
	protected static final Logger log = Logger.getLogger(MinaClient.class);
	
	protected IoSession session;
	
	protected final IContentHandler contentHandler;
	
	protected final IContentFactory contentFactory;
	
	public MinaClientHandler(IContentHandler contentHandler, IContentFactory contentFactory) {
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		log.info(cause, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		ByteBuffer in = (ByteBuffer) message;
		byte[] data = new byte[in.remaining()];
	    in.get(data);
		IContent content = contentFactory.createContent(data, new MinaTcpSender(session));
		if (content != null) {
			contentHandler.handle(content);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.info("Net close.");
//		MinaClient.this.connected = false;
//		if (MinaClient.this.needRetry && MinaClient.this.valid) {
//			MinaClient.this.valid = false;
//			while (!MinaClient.this.connected) {
//				try {
//					Thread.sleep(3000L);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				try {
//					MinaClient.this.initConnector();
//					MinaClient.this.connect();
//					MinaClient.log.info("breaked");
//				} catch (Exception e) {
//					log.error("", e);
//				}
//			}
//		} else {
//			MinaClient.this.valid = false;
//		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		this.session = session;
//		MinaClient.this.session = session;
//		MinaClient.this.connected = true;
//		MinaClient.this.valid = true;
//		if (!MinaClient.this.needSecureAuth) {
//			synchronized (MinaClient.this.lock) {
//				MinaClient.this.lock.notify();
//			}
//		}
	}
	
	public void send(Object object) {
		session.write(object);
	}
	
	public void close() {
		if (session != null && session.isConnected()) {
			session.getCloseFuture().setClosed();
			session.close();
		}
	}
	
	public boolean isConnected() {
		return session != null && session.isConnected();
	}

}
