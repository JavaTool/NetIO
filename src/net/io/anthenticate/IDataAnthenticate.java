package net.io.anthenticate;

public interface IDataAnthenticate<I, O> {
	
	void write(O out) throws Exception;
	
	boolean read(I in);
	
	int getAnthenticateLength();

}
