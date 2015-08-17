package net.io.mina.server;

import java.util.Map;
import java.util.TreeMap;

public class HandlerDispatchManager {
	
	protected static Map<String, HandlerDispatch> dispatchs = new TreeMap<String, HandlerDispatch>();
	
	public static void add(HandlerDispatch dispatch){
		dispatchs.put(dispatch.getId(), dispatch);
	}
	
	public static HandlerDispatch remove(String id){
		return dispatchs.remove(id);
	}
	
	public static HandlerDispatch get(String id){
		return dispatchs.get(id);
	}
	
	public static boolean register(String id,int opcode,PacketHandler handler){
		HandlerDispatch dispatch = get(id);
		if(dispatch == null)
			return false;
		dispatch.register(opcode, handler);
		return true;
	}
	
	public static boolean register(String id,int[] opcodes,PacketHandler handler){
		HandlerDispatch dispatch = get(id);
		if(dispatch == null)
			return false;
		dispatch.register(opcodes, handler);
		return true;
	}
	
	public static void unRegister(String id,int opcode){
		HandlerDispatch dispatch = get(id);
		if(dispatch != null)
			dispatch.unRegister(opcode);
	}
	
	public static void unRegister(String id,int[] opcodes){
		HandlerDispatch dispatch = get(id);
		if(dispatch != null)
			dispatch.unRegister(opcodes);
	}
	
}
