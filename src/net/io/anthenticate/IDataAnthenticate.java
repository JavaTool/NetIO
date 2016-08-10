package net.io.anthenticate;

/**
 * <p>
 * Add anthenticate and verify datas.<br>
 * Excample : Add a head to content.
 * </p>
 * @author	hyfu
 * @param 	<I>
 * 			The type of need to verify's datas. 
 * @param 	<O>
 * 			The type of anthenticate object.
 */
public interface IDataAnthenticate<I, O> {
	
	/**
	 * Add anthenticate datas into object.
	 * @param 	out
	 * 			An object which can add anthenticate datas.
	 * @throws 	Exception
	 * 			Anthenticate maybe throw some exception.
	 */
	void write(O out) throws Exception;
	/**
	 * Verify datas.
	 * @param 	in
	 * 			Need to verify's datas.
	 * @return	Verify result.
	 */
	boolean read(I in);
	/**
	 * Get the length of anthenticate part.
	 * @return	The length of anthenticate part.
	 */
	int getAnthenticateLength();

}
