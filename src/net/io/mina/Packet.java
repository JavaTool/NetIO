package net.io.mina;

import java.nio.charset.Charset;

import org.apache.mina.common.ByteBuffer;

public class Packet {

	public static final byte[] HEAD = {'U','A'};
	
	protected static final Charset utf8 = Charset.forName("utf-8");
	
	protected static final Charset utf16be = Charset.forName("UTF-16BE");
	
//	private static final CharsetEncoder encoder = utf8.newEncoder();
	
//	private static final CharsetDecoder decoder = utf8.newDecoder();
	
	protected int opCode;
	
	protected ByteBuffer data;

	// �Ƿ���Ҫ�ײ����
	public boolean needEncrypt = false;
	
	public Packet(int opCode){
		this.opCode = opCode;
		this.data = ByteBuffer.allocate(128);
		data.setAutoExpand(true);
	}
	
	public Packet(int opCode, ByteBuffer data){
		this.opCode = opCode;
		this.data = data;
	}
	
	public int getOpCode(){
		return opCode;
	}
	
	public void put(byte value){
		data.put(value);
	}
	
	public byte get(){
		return data.get();
	}
	
	public byte getByte(){
		return data.get();
	}
	
	public void put(byte[] value){
		data.putInt(value.length);
		data.put(value);
	}
	
	public void put(byte[] value,int off,int length){
		data.putInt(length);
		data.put(value,off,length);
	}
	
	public void putPlain(byte[] value){
		data.put(value);
	}
	
	public void putInts(int[] value){
		data.putShort((short)value.length);
		for(int i=0;i<value.length;i++){
			data.putInt(value[i]);
		}
	}
	
	public void putShorts(short[] value){
		data.putShort((short)value.length);
		for(int i=0;i<value.length;i++){
			data.putShort(value[i]);
		}
	}
	
	public byte[] getBytes(){
		int len = data.getInt();
		byte[] ret = new byte[len];
		data.get(ret);
		return ret;
	}
	
	public void putShort(short value){
		data.putShort(value);
	}
	
	public short getShort(){
		return data.getShort();
	}
	
	public void putInt(int value){
		data.putInt(value);
	}
	
	public int getInt(){
		return data.getInt();
	}
	
	public int[] getInts() {
		short len = data.getShort();
		int[] ret = new int[len];
		for (int i = 0; i < len; i++) {
			ret[i] = data.getInt();
		}
		return ret;
	}
	
	public void putLong(long value){
		data.putLong(value);
	}
	
	public long getLong(){
		return data.getLong();
	}
	
	public int get(byte[] bs) {
		ByteBuffer ret = data.get(bs, 0, bs.length);
		return ret.remaining();
	}

	public int getUnsignedByte() {
		return data.getUnsigned();
	}

	public int getUnsignedShort() {
		return data.getUnsignedShort();
	}

	public void put(int b) {
		data.put((byte)b);
	}

	public void putShort(int s) {
		data.putShort((short)s);
	}

	public void putString(String s) {
//		byte[] bytes = s.getBytes(utf8);
//		data.putShort((short)bytes.length);
//		data.put(bytes);
        byte[] bytes = s.getBytes(utf8);
		byte[] bytes2 = s.getBytes(utf16be);
		if (bytes.length < bytes2.length) {
		    if (bytes.length > 32767) {
		        throw new IllegalArgumentException();
		    }
            data.putShort((short)bytes.length);
            data.put(bytes);
		} else {
            if (bytes2.length > 32767) {
                throw new IllegalArgumentException();
            }
		    data.putShort((short)(bytes2.length | 0x8000));
		    data.put(bytes2);
		}
	}
	
	public void putUTF(String s) {
	    byte[] bytes = s.getBytes(utf8);
        data.putShort((short)bytes.length);
        data.put(bytes);
	}
	
	public String getString() {
	    int len = data.getShort() & 0xFFFF;
	    if ((len & 0x8000) == 0) {
	        byte[] buf = new byte[len];
	        data.get(buf);
	        return new String(buf, utf8);
	    } else {
	        len &= 0x7FFF;
	        byte[] buf = new byte[len];
            data.get(buf);
            return new String(buf, utf16be);
	    }
	}
	
	public ByteBuffer getData(){
		return data;
	}

}
