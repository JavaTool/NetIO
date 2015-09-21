package net.io.netty.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.dipatch.Content;
import net.dipatch.IContent;
import net.dipatch.ISender;
import net.io.IContentFactory;
import net.io.http.IHttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientContentFactory implements IContentFactory {
	
	protected static final String SESSION_ID = "SESSION_ID";
	
	protected static final Logger log = LoggerFactory.getLogger(NettyClientContentFactory.class);

	@Override
	public IContent createContent(byte[] data, IHttpSession httpSession) {
		return createContent(data, httpSession.getSender());
	}

	@Override
	public IContent createContent(byte[] data, ISender sender) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		try {
			int messageId = dis.readInt();
			int messageLength = dis.readInt();
			byte[] datas = new byte[messageLength];
			dis.read(datas);
			String sessionId = sender.getAttribute(SESSION_ID, String.class);
			sessionId = sessionId == null ? "" : sessionId;
			return new Content(sessionId, messageId, datas, sender);
		} catch (IOException e) {
			log.error("", e);
			return null;
		}
	}

}
