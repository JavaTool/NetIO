package net.io.mina.server;

import static net.content.IContentFactory.SESSION_ID;

import java.util.UUID;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.content.IContent;
import net.content.IContentFactory;
import net.content.IContentHandler;
import net.io.ISender;

public class MinaIoHandler extends IoHandlerAdapter {
	
	protected static final String SENDER_KEY = "SENDER_KEY";
	
	protected static final Logger log = LoggerFactory.getLogger(MinaIoHandler.class);
	
	protected final IContentHandler contentHandler;
	
	protected final IContentFactory contentFactory;
	
	public MinaIoHandler(IContentHandler contentHandler, IContentFactory contentFactory) {
		this.contentHandler = contentHandler;
		this.contentFactory = contentFactory;
	}
	
    @Override
    public void exceptionCaught(IoSession session, Throwable t) throws Exception {
        log.debug(t.getMessage(), t);
    }
    
    @Override
    public void messageReceived(IoSession session, Object msg) throws Exception {
		ISender sender = (ISender) session.getAttribute(SENDER_KEY);
		if (sender == null) {
			sender = new MinaSender(session);
			session.setAttribute(SENDER_KEY, sender);
			session.setAttribute(SESSION_ID, UUID.randomUUID().toString());
		}

		ByteBuffer in = (ByteBuffer) msg;
	    byte[] data = new byte[in.remaining()];
	    in.get(data);
		IContent content = contentFactory.createContent(data, sender);
		if (content != null) {
			contentHandler.handle(content);
		}
    }
    
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        String address = session.getRemoteAddress().toString();
		String sessionId = session.getAttribute(SESSION_ID).toString();
		log.info("[Coming Out]IP:{}", address);
		session.close();
		if (sessionId != null) {
			contentHandler.disconnect(sessionId, address);
		}
    }
    
    @Override
    public void sessionCreated(IoSession session) throws Exception {
//        // 检查IP地址
//        TrustIpService ts = Platform.getAppContext().get(TrustIpService.class);
//        if (!ts.isTrustIp((InetSocketAddress)session.getRemoteAddress())) {
//            session.close();
//            return;
//        }
//        
//        session.setAttribute(SESSION_ID, ids.incrementAndGet());
//        session.setAttribute(SESSION_COUNTER, new SyncInteger(0));
    }

}
