package net.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Map;

import net.io.http.client.IHttpConnector;

class FileInputStreamConnector implements IHttpConnector<InputStream> {
	
	private static final int CODE_FILE_OK = 200;
	
	private static final int CODE_FILE_NOT_EXISTS = 400;
	
	private File file;
	
	private IResponseCodeHandler responseCodeHandler;
	
	public FileInputStreamConnector() {
		setResponseCodeHandler(code -> {if (code >= CODE_FILE_NOT_EXISTS) throw new Exception("code is : " + code);});
	}

	@Override
	public void setUrl(String url) {
		file = new File(url);
	}

	@Override
	public void setResponseCodeHandler(IResponseCodeHandler handler) {
		this.responseCodeHandler = handler;
	}

	@Deprecated
	@Override
	public String getCookie() {
		return null;
	}

	@Override
	public InputStream post(String params, byte[] bytes, Map<String, Object> head) throws Exception {
		if (file.exists()) {
			fileOk();
			if (params.length() > 0) {
				try (FileWriter fileWriter = new FileWriter(file);) {
					fileWriter.write(params);
					fileWriter.flush();
				}
			}
			if (bytes.length > 0) {
				try (FileOutputStream fos = new FileOutputStream(file);) {
					fos.write(bytes);
					fos.flush();
				}
			}
		} else {
			fileNotExists();
		}
		return null;
	}
	
	private void fileNotExists() throws Exception {
		responseCodeHandler.handleResponseCode(CODE_FILE_NOT_EXISTS);
	}
	
	private void fileOk() throws Exception {
		responseCodeHandler.handleResponseCode(CODE_FILE_OK);
	}

	@Override
	public InputStream get(String params, Map<String, Object> head) throws Exception {
		if (file.exists()) {
			fileOk();
			return new FileInputStream(file);
		} else {
			fileNotExists();
			return null;
		}
	}

	@Override
	public InputStream put(String params, byte[] bytes, Map<String, Object> head) throws Exception {
		if (file.exists()) {
			fileOk();
		} else {
			fileNotExists();
		}
		return null;
	}

	@Override
	public InputStream delete(String params, Map<String, Object> head) throws Exception {
		if (file.exists()) {
			fileOk();
		} else {
			fileNotExists();
		}
		file.delete();
		return null;
	}

}
