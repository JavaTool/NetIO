package net.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public interface INetClient {
	
	void connect(byte[] data);
	
	void close() throws Exception;
	
	public static void sendMessage(INetClient connection, IMessage message) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bout);
		dos.writeInt(message.getMessageId());
		byte[] datas = message.getByteArray();
		dos.writeInt(datas.length);
//		dos.write(EncryptUtil.encrypt(resultMessage, resultMessage.length, EncryptUtil.PASSWORD));
		dos.write(datas);
		byte[] bytes = bout.toByteArray();
		connection.connect(bytes);
	}

}
