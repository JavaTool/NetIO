package net.io.mina.server;

import net.io.mina.Packet;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MinaUAEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out) throws Exception {
		if (obj instanceof Packet) {
			Packet packet = (Packet)obj;
			ByteBuffer data = packet.getData();
			data.flip();
			int len = 8+data.remaining();
			ByteBuffer buf = ByteBuffer.allocate(len);
			buf.put(Packet.HEAD);
			buf.putInt(len);
			buf.putInt(packet.getOpCode());
			buf.put(data);
			buf.flip();
			out.write(buf);
		}
	}

}
