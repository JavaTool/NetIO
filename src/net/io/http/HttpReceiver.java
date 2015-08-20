package net.io.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.io.MessageHandle;
import net.io.RedirectResponse;
import net.io.Response;
import net.io.util.HttpConnectUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http消息接收器
 * @author 	fuhuiyuan
 */
public abstract class HttpReceiver extends HttpServlet implements HTTPStatus {
	
	private static final Logger log = LoggerFactory.getLogger(HttpReceiver.class);

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		OutputStream os = response.getOutputStream();
		Response resp;
		try {
			HttpSession session = req.getSession();
			log.info("Session id is {}.", session.getId());
			int messageId = Integer.parseInt(req.getHeader(HttpConnectUtil.MESSAGEID));
			byte[] decrypt = HttpConnectUtil.getRequestProtoContent(req);
			AsyncContext asyncContext = req.startAsync();
			MessageHandle opcodeHandle = ((MessageHandle) req.getServletContext().getAttribute(MessageHandle.class.getName()));
			resp = opcodeHandle.handle(decrypt, getIpAddr(req), messageId, session.getId(), null);
		} catch (Exception e) {
			error(e, response, os);
			os.flush();
			os.close();
			return;
		}
		
		if (resp instanceof RedirectResponse) {
//			response.sendRedirect(((RedirectResponse) resp).getUrl());
			req.getRequestDispatcher(((RedirectResponse) resp).getUrl()).forward(req, response);
		} else {
			try {
				int sendMessageId = resp.getSendMessageId();
				response.setContentType("text/plain; charset=UTF-8; MessageId=" + sendMessageId);
				log.info("sendMessageId = " + sendMessageId);
				response.setStatus(resp.getStatus());
				resp.output(os);
			} catch (Exception e) {
				error(e, response, os);
			} finally {
				os.flush();
				os.close();
			}
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
		Response errorResponse = createErrorResponse(e);
		response.setContentType("text/plain; charset=UTF-8; " + HttpConnectUtil.MESSAGEID + "=" + errorResponse.getSendMessageId());
		response.setStatus(HTTP_STATUS_SUCCESS);
		errorResponse.output(os);
	}
	
	protected abstract Response createErrorResponse(Exception e);
	
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
