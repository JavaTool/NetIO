package net.io.protocal.proto.output;

public class ProtoMessageField {
	
	public final String name;
	
	public final String className;
	
	public final String type;
	
	private String annotate;
	
	public ProtoMessageField(String name, String className, String type) {
		this.name = name;
		this.className = className;
		this.type = type;
		setAnnotate("<u>do not has any annotate.</u>");
	}

	public String getAnnotate() {
		return annotate;
	}

	public void setAnnotate(String annotate) {
		this.annotate = annotate;
	}

}
