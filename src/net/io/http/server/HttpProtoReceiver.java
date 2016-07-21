package net.io.http.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.io.ISender;
import net.io.dispatch.Content;
import net.io.dispatch.IContent;
import net.io.dispatch.IContentHandler;
import net.io.http.HttpStatus;
import net.util.HttpConnectUtil;

/**
 * Proto消息接收器
 * @author 	fuhuiyuan
 */
public class HttpProtoReceiver extends HttpServlet implements HttpStatus {
	
	private static final Logger log = LoggerFactory.getLogger(HttpProtoReceiver.class);

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
		
		ISender sender = new HttpResponseSender(response, session);
		try {
			int messageId = Integer.parseInt(req.getHeader("MessageId"));
			byte[] decrypt = HttpConnectUtil.getRequestProtoContent(req);
			
			IContent content = new Content(getSessionId(req), messageId, decrypt, sender);
			System.out.println("===========================");
			System.out.println(content.getMessageId());
			System.out.println(content.getSessionId());
			System.out.println("===========================");
			IContentHandler contentHandler = (IContentHandler) req.getServletContext().getAttribute(IContentHandler.class.getName());
			contentHandler.handle(content);
		} catch (Exception e) {
			error(e, response, os);
			os.flush();
			os.close();
			return;
		}
	}
	
	protected String getSessionId(HttpServletRequest req) {
		return req.getSession().getId();
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
//		VO_Error.Builder builder = VO_Error.newBuilder();
//		builder.setErrorCode(0);
//		builder.setErrorMsg("Unprocessor exception.");
//		response.setContentType("text/plain; charset=UTF-8; " + MessageId.class.getSimpleName() + "=" + MessageId.MIVO_Error.name());
//		response.setStatus(HTTP_STATUS_SUCCESS);
//		os.write(builder.build().toByteArray());
	}
	
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
