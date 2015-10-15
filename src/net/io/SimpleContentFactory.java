package net.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.dipatch.Content;
import net.dipatch.IContent;
import net.dipatch.ISender;
import net.io.http.IHttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleContentFactory implements IContentFactory {
	
	protected static final Logger log = LoggerFactory.getLogger(SimpleContentFactory.class);

	@Override
	public IContent createContent(byte[] data, IHttpSession httpSession) {
		String sessionId = httpSession.getId();
		int messageId = httpSession.getMessageId();
		return new Content(sessionId, messageId, data, httpSession.getSender());
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
