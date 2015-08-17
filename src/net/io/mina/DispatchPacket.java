package net.io.mina;

import java.util.HashSet;
import java.util.Set;

public class DispatchPacket {
	
	public static final Set<Short> URGENT_SET = new HashSet<Short>();

	public static final byte[] HEAD = { 'D', 'A' };

	protected int id;
	
	protected Packet packet;

	public DispatchPacket(int id, Packet packet) {
		this.id = id;
		if (URGENT_SET.contains(packet.opCode)) {
			packet.opCode |= 1 << 15;
		}
		this.packet = packet;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public int getId() {
		return id;
	}
	
}
