package net.io.netty.client;

import io.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.io.MessageHandle;
import net.io.netty.Packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyPacketClient extends NettyClient {
	
	protected static final Logger log = LoggerFactory.getLogger(NettyPacketClient.class);
	
	public NettyPacketClient(final MessageHandle messageHandle, int port, String host) throws Exception {
		super(new NettyClientCallback() {

			@Override
			public void callback(byte[] data, String ip, Channel channel) {
				try {
					DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
					int messageId = dis.readInt();
					int messageLength = dis.readInt();
					byte[] value = new byte[messageLength];
					dis.read(value);
					messageHandle.handle(data, ip, messageId, null, channel);
				} catch (Exception e) {
					log.error("", e);
				}
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
