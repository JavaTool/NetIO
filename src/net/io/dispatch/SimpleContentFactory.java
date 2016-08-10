package net.io.dispatch;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.io.ISender;
import net.io.anthenticate.IDataAnthenticate;

/**
 * The simple implement of {@link IContentFactory}.
 * @author	hyfu
 */
public class SimpleContentFactory implements IContentFactory {
	
	protected static final Logger log = LoggerFactory.getLogger(SimpleContentFactory.class);
	
	protected final IDataAnthenticate<byte[], DataOutputStream> dataAnthenticate;
	
	public SimpleContentFactory(IDataAnthenticate<byte[], DataOutputStream> dataAnthenticate) {
		this.dataAnthenticate = dataAnthenticate;
	}

	@Override
	public IContent createContent(String sessionId, int messageId, byte[] datas, ISender sender) {
		return new Content(sessionId, messageId, datas, sender);
	}

	@Override
	public IContent createContent(byte[] data, ISender sender) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		try {
			return createContent(dis, sender);
		} catch (IOException e) {
			log.error("", e);
			return null;
		}
	}
	
	/**
	 * Create content by a stream.
	 * @param 	dis
	 * 			The input stream.
	 * @param 	sender
	 * @return	A content.
	 * @throws 	IOException
	 */
	protected IContent createContent(DataInputStream dis, ISender sender) throws IOException {
		int messageId = dis.readInt();
		int messageLength = dis.readInt();
		byte[] datas = new byte[messageLength];
		dis.read(datas);
		String sessionId = sender.getAttribute(SESSION_ID, String.class);
		sessionId = sessionId == null ? "" : sessionId;
		return createContent(sessionId, messageId, datas, sender);
	}

	@Override
	public IDataAnthenticate<byte[], DataOutputStream> getDataAnthenticate() {
		return dataAnthenticate;
	}

}
