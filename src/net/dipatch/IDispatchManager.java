package net.dipatch;

public interface IDispatchManager {
	
	void addDispatch(IContent content);
	
	void fireDispatch(IContent content);

}
