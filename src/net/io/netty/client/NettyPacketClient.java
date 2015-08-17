package net.io.netty.client;

import io.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.io.MessageHandle;
import net.io.netty.Packet;

public class NettyPacketClient extends NettyClient {
	
	public NettyPacketClient(final MessageHandle messageHandle, int port, String host) throws Exception {
		super(new NettyClientCallback() {

			@Override
			public void callback(byte[] data, String ip, Channel channel) throws Exception {
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
				int messageId = dis.readInt();
				int messageLength = dis.readInt();
				byte[] value = new byte[messageLength];
				dis.read(value);
				messageHandle.handle(data, ip, messageId, null, channel);
			}
			
		}, port, host);
	}
	
	public void connect(int messageId, byte[] data) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(messageId);
		dos.writeInt(data.length);
		dos.write(data);
		connect(baos.toByteArray());
	}
	
	public void connect(Packet packet, byte[] data) throws Exception {
		connect(packet.getMessageId(), packet.getValue());
	}

}
