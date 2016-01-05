package net.io.mina.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;

import net.io.ISender;

public class MinaSender implements ISender {
	
	protected final IoSession session;
	
	public MinaSender(IoSession session) {
		this.session = session;
	}

	@Override
	public void send(byte[] datas, int messageId) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bout);
		dos.writeInt(datas.length + 8);
		dos.writeInt(messageId);
		dos.writeInt(datas.length);
//		dos.write(EncryptUtil.encrypt(resultMessage, resultMessage.length, EncryptUtil.PASSWORD));
		dos.write(datas);
		byte[] bytes = bout.toByteArray();
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		session.write(buf);
	}

	@Override
	public <X, Y extends X> void setAttribute(String key, Class<X> clz, Y value) {
		session.setAttribute(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X getAttribute(String key, Class<X> clz) {
		return (X) session.getAttribute(key);
	}

	@Override
	public String getIp() {
		return session.getRemoteAddress().toString();
	}

}
