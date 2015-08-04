package net.io.proto.output;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

public class ProtoScaner {
	
	public void scan(File file, Map<String, ProtoMessage> messages) throws Exception {
		if (file.isFile()) {
			scanProto(file, messages);
		} else {
			for (File subFile : file.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					String name = pathname.getName();
					return name.endsWith(".proto") || pathname.isDirectory() && name.equals("MessageId.proto");
				}
				
			})) {
				scan(subFile, messages);
			}
		}
	}
	
	public void scanProto(File file, Map<String, ProtoMessage> messages) throws Exception {
		
	}

}
