package net.dipatch;

public interface Sender {
	
	void send(byte[] datas) throws Exception;
	
	byte[] getSendDatas();

}
