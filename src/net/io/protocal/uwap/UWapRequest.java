package net.io.protocal.uwap;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import net.dipatch.ISender;
import net.io.Request;

final class UWapRequest implements Request {
	
	protected static final byte[] HEAD = {'U', 'A'};
	
	protected static final byte[] PROTOCOL = {'D', 'A'};
	
	/**请求信息的id*/
	protected final int receiveMessageId;
	
	protected final byte[] datas;
	/**数据发送器*/
	protected final ISender sender;
	
	protected final String sessionId;
	
	protected UWapRequest(int receiveMessageId, byte[] datas, ISender sender, String sessionId) {
		this.receiveMessageId = receiveMessageId;
		this.datas = datas;
		this.sender = sender;
		this.sessionId = sessionId;
		read(createStream());
	}
	
	protected UWapRequest(int receiveMessageId, InputStream is, int contentLength, ISender sender, String sessionId) throws Exception {
		this.sender = sender;
		this.sessionId = sessionId;
		DataInput input = new DataInputStream(is);
		try {
			byte protocol_0 = input.readByte();
			byte protocol_1 = input.readByte();
			if (protocol_0 == PROTOCOL[0] && protocol_1 == PROTOCOL[1]) {
				int len = input.readInt();
				input.readInt();
				byte head_0 = input.readByte();
				byte head_1 = input.readByte();
				if (head_0 == HEAD[0] && head_1 == HEAD[1]) {
					int length = input.readInt();
					this.receiveMessageId = input.readInt();
					if (len > 20) {
						datas = new byte[length - 10];
						is.read(datas);
					} else {
						datas = new byte[0];
					}
					read(createStream());
				} else {
					throw new Exception("Head error : " + (char) head_0 + (char) + head_1);
				}
			} else {
				throw new Exception("Protocol error : " + (char) protocol_0 + (char) + protocol_1);
			}
		} finally {
			input = null;
		}
	}
	
	protected DataInput createStream() {
		return new DataInputStream(new ByteArrayInputStream(getByteArray()));
	}
	
	protected void read(DataInput di) {}

	@Override
	public int getReceiveMessageId() {
		return receiveMessageId;
	}

	@Override
	public byte[] getByteArray() {
		return datas;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public ISender getSender() {
		return sender;
	}

}
