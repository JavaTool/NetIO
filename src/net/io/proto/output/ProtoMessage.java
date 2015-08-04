package net.io.proto.output;

import java.util.HashMap;
import java.util.Map;

public class ProtoMessage {
	
	private String messageIdName;
	
	private int messageIdValue;
	
	private Map<String, ProtoMessageField> fields = new HashMap<String, ProtoMessageField>();

	public String getMessageIdName() {
		return messageIdName;
	}

	public void setMessageIdName(String messageIdName) {
		this.messageIdName = messageIdName;
	}

	public int getMessageIdValue() {
		return messageIdValue;
	}

	public void setMessageIdValue(int messageIdValue) {
		this.messageIdValue = messageIdValue;
	}
	
	public void addField(ProtoMessageField protoMessageField) {
		fields.put(protoMessageField.name, protoMessageField);
	}

	public void setFields(Map<String, ProtoMessageField> fields) {
		this.fields = fields;
	}

}
