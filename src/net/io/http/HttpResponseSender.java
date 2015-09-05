package net.io.http;

import java.io.OutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import net.dipatch.ISender;

/**
 * HTTP响应发送器
 * @author 	fuhuiyuan
 */
public class HttpResponseSender implements ISender {
	
	/**响应*/
	private final ServletResponse response;
	/**Http会话*/
	private final HttpSession session;
	
	public HttpResponseSender(ServletResponse response, HttpSession session) {
		this.response = response;
		this.session = session;
	}

	@Override
	public void send(byte[] datas, String messageId) throws Exception {
		OutputStream os = response.getOutputStream();
		try {
			response.setContentType("text/plain; charset=UTF-8; MessageId=" + messageId);
			os.write(datas);
		} finally {
			os.flush();
			os.close();
		}
	}

	@Override
	public <X, Y extends X> void setAttribute(String key, Class<X> clz, Y value) {
		session.setAttribute(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X getAttribute(String key, Class<X> clz) {
		return (X) session.getAttribute(key);
	}

}
