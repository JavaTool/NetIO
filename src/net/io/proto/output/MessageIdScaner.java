package net.io.proto.output;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class MessageIdScaner {
	
	private static final String HEAD = "MI";
	
	private static final String CLIENT_TO_SERVER = HEAD + "CS_";
	
	private static final String SERVER_TO_CLIENT = HEAD + "SC_";
	
	private static final String PROXY = HEAD + "PX_";
	
	public Map<String, ProtoMessage> scan(File file) throws Exception {
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		Map<String, ProtoMessage> messages = new HashMap<String, ProtoMessage>();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				int start = line.indexOf(HEAD);
				int end = line.indexOf(';');
				if (start > -1 && end > -1) {
					line = line.substring(start, end).trim();
					String[] infos = line.split("=");
					ProtoMessage protoMessage = new ProtoMessage();
					if (infos[0].startsWith(CLIENT_TO_SERVER)) {
						
					} else if (infos[0].startsWith(SERVER_TO_CLIENT)) {
						
					} else if (infos[0].startsWith(PROXY)) {
						
					} else {
						throw new Exception("Unknow line : " + line);
					}
					protoMessage.setMessageIdName(infos[0]);
					protoMessage.setMessageIdValue(Integer.parseInt(infos[1]));
					messages.put(infos[0], protoMessage);
				}
			}
		} finally {
			reader.close();
		}
		return messages;
	}

}
