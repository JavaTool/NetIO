package net.io.mina.server;

import net.io.mina.DispatchPacket;
import net.io.mina.Packet;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class DispatchUAEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out) throws Exception {
		if (obj instanceof DispatchPacket) {
			DispatchPacket dp = (DispatchPacket) obj;
			Packet packet = dp.getPacket();
			ByteBuffer data = packet.getData();
			data.flip();
			int len = 20 + data.remaining();
			ByteBuffer buf = ByteBuffer.allocate(len);
			buf.put(DispatchPacket.HEAD);
			buf.putInt(len);
			buf.putInt(dp.getId());
			buf.put(Packet.HEAD);
			buf.putInt(len - 10);
			buf.putInt(packet.getOpCode());
			buf.put(data);
			buf.flip();
			out.write(buf);
		}
	}
	
}
