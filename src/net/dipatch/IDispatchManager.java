package net.dipatch;

public interface IDispatchManager<T extends IContent> {
	
	void addDispatch(T content);
	
	void fireDispatch(T content);

}
