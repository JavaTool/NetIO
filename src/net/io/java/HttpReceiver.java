package net.io.java;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.content.Content;
import net.content.IContent;
import net.content.IContentHandler;
import net.io.ISender;
import net.message.IMessage;
import net.util.HttpConnectUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http消息接收器
 * @author 	fuhuiyuan
 */
public abstract class HttpReceiver extends HttpServlet implements HttpStatus {
	
	private static final Logger log = LoggerFactory.getLogger(HttpReceiver.class);

	private static final long serialVersionUID = 1L;
	
	public static final String SESSION_IP = "sessionIp";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		OutputStream os = response.getOutputStream();
		HttpSession session = req.getSession();
		String ip = getIpAddr(req);
		
		if (session.getAttribute(SESSION_IP) == null) {
			session.setAttribute(SESSION_IP, ip);
		}
		
		ISender sender = new HttpResponseSender(response, session, ip);
		try {
			log.info("Session id is {} : {}.", session.getId(), ip);
			int messageId = Integer.parseInt(req.getHeader("MessageId"));
			byte[] decrypt = HttpConnectUtil.getRequestProtoContent(req);
			
			IContent content = new Content(session.getId(), messageId, decrypt, sender);
			IContentHandler contentHandler = (IContentHandler) req.getServletContext().getAttribute(IContentHandler.class.getName());
			contentHandler.handle(content);
		} catch (Exception e) {
			error(e, response, os);
			os.flush();
			os.close();
			return;
		}
	}
	
	/**
	 * 处理异常
	 * @param 	e
	 * 			异常
	 * @param 	response
	 * 			响应
	 * @param 	os
	 * 			输出流
	 * @throws 	IOException
	 */
	private void error(Exception e, HttpServletResponse response, OutputStream os) throws IOException {
		log.error("", e);
		IMessage errorResponse = createErrorResponse(e);
		response.setContentType("text/plain; charset=UTF-8; " + HttpConnectUtil.MESSAGEID + "=" + errorResponse.getMessageId());
		response.setStatus(HTTP_STATUS_SUCCESS);
		errorResponse.output(os);
	}
	
	protected abstract IMessage createErrorResponse(Exception e);
	
	/**
	 * 获取解析的地址
	 * @param 	request
	 * 			请求
	 * @return	地址
	 */
	private static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
