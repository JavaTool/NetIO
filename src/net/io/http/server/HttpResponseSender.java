package net.io.http.server;

import java.io.OutputStream;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import net.io.ISender;

/**
 * HTTP响应发送器
 * @author 	fuhuiyuan
 */
public class HttpResponseSender implements ISender {
	
	private static final String CLOSE_EXCEPTION = "Closed";
	/**响应*/
	private final ServletResponse response;
	/**Http会话*/
	private final HttpSession session;
	
	public HttpResponseSender(ServletResponse response, HttpSession session) {
		this.response = response;
		this.session = session;
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

	@Override
	public void send(byte[] datas, int messageId) throws Exception {
		OutputStream os = response.getOutputStream();
		try {
//			response.setContentType(makeHead(messageId, useTime));
			os.write(datas);
		} catch (Exception e) {
			if (CLOSE_EXCEPTION.equals(e.getMessage())) {
				// unprocess close exception
			} else {
				throw e;
			}
		} finally {
			os.flush();
			os.close();
		}
	}

	@Override
	public String getIp() {
		// TODO Auto-generated method stub
		return null;
	}

}
