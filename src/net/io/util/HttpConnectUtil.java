package net.io.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.io.http.HttpStatus;
import net.io.http.HttpBackInfo;

/**
 * HTTP连接工具
 * @author	fuhuiyuan
 */
public abstract class HttpConnectUtil implements HttpStatus {
	
	/**HTTP消息头的消息id名称*/
	public static final String MESSAGEID = "MessageId";
	
	private static final byte[] NULL_REQUEST = new byte[0];
	/**最大数据读取次数*/
	private static final int CONTENT_MAX_READ_TIMES = 5;
	
	/**
	 * 创建连接
	 * @param 	url
	 * 			链接
	 * @return	连接
	 * @throws 	IOException
	 */
	public static HttpURLConnection createConnection(String url) throws IOException {
		URL targetUrl = new URL(url);  
		HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();  
		connection.setDoOutput(true);  
		connection.setDoInput(true);  
		connection.setRequestProperty("Content-Type", "application/x-protobuf");  
		connection.setRequestProperty("Accept", "application/x-protobuf");  
		connection.setRequestMethod("POST");
		return connection;
	}
	
	/**
	 * 获取连接返回内容
	 * @param 	connection
	 * 			连接
	 * @param 	content
	 * 			发送内容
	 * @return	HTTP反馈信息
	 * @throws 	IOException
	 */
	public static HttpBackInfo getConnectionStream(HttpURLConnection connection, byte[] content) throws IOException {
		connection.setRequestProperty("Connect-Length", Integer.toString(content.length));  
        connection.setFixedLengthStreamingMode(content.length);
        OutputStream outputStream = connection.getOutputStream();  
        outputStream.write(content);  
        outputStream.flush();  
        outputStream.close();
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HTTP_STATUS_SUCCESS || responseCode == HTTP_STATUS_REDIRECT) {
	        InputStream is = connection.getInputStream();
			byte[] b = new byte[4096];
			int n;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((n = is.read(b, 0, 4096)) > 0) {
				baos.write(b, 0, n);
			}
			is.close();
			
			String messageId = "";
			for (String contentType : connection.getContentType().split(";")) {
				String[] infos = contentType.trim().split("=", -2);
				if (infos[0].equals(MESSAGEID)) {
					messageId = infos[1];
					break;
				}
			}
			
			return new HttpBackInfo(baos.toByteArray(), responseCode, messageId);
        } else {
        	throw new IOException("Connection response code is " + responseCode + ", connection is " + connection.getURL().toString() + ".");
        }
	}
	
	/**
	 * 获取连接返回内容
	 * @param 	connection
	 * 			连接
	 * @param 	content
	 * 			发送内容
	 * @param 	messageId
	 * 			消息id
	 * @return	HTTP反馈信息
	 * @throws 	IOException
	 */
	public static HttpBackInfo getConnectionStream(String url, byte[] content, String messageId) throws IOException {
		HttpURLConnection connection = createConnection(url);
		connection.setRequestProperty(MESSAGEID, messageId);
		return getConnectionStream(connection, content);
	}

	/**
	 * 获取连接返回内容，并保存Cookie
	 * @param 	connection
	 * 			连接
	 * @param 	content
	 * 			发送内容
	 * @param 	messageId
	 * 			消息id
	 * @return	HTTP反馈信息
	 * @throws 	IOException
	 */
	public static HttpBackInfo getConnectionStreamSaveCookie(String url, byte[] content, String messageId) throws IOException, URISyntaxException {
		HttpURLConnection connection = createConnection(url);
		connection.setRequestProperty(MESSAGEID, messageId);
		HttpBackInfo httpBackInfo = getConnectionStream(connection, content);
		String strcoo = getCookies(connection);
		saveCookies(connection.getURL().toURI(), strcoo);
		return httpBackInfo;
	}
	
	/**
	 * 重定向
	 * @param 	req
	 * 			请求
	 * @param 	resp
	 * 			响应
	 * @param 	url
	 * 			链接
	 * @throws 	IOException
	 */
	public static void sendRedirect(HttpServletRequest req, HttpServletResponse resp, String url) throws IOException {
		resp.sendRedirect(url);
	}
	
	/**
	 * 保存Cookie
	 * @param 	uri
	 * 			链接
	 * @param 	strcoo
	 * 			Cookie
	 */
	public static void saveCookies(URI uri, String strcoo) {
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		// 接受 HTTP 请求的时候，得到和保存新的 Cookie  
        HttpCookie cookie = new HttpCookie("Cookie: ", strcoo);  
        //cookie.setMaxAge(60000);//没这个也行。  
        cookieManager.getCookieStore().add(uri, cookie);
	}
	   
	/**
	 * 获取Cookie
	 * @param 	cookieManager
	 * 			Cookie管理器
	 * @return	Cookie
	 */
	public static HttpCookie getcookies(CookieManager cookieManager){  
		HttpCookie res = null;
		// 使用 Cookie 的时候： 
		// 取出 CookieStore
		CookieStore store = cookieManager.getCookieStore();

	    // 得到所有的 URI
	    List<URI> uris = store.getURIs();
	    for (URI ur : uris) {
	        // 筛选需要的 URI
	        // 得到属于这个 URI 的所有 Cookie
	        List<HttpCookie> cookies = store.get(ur);
	        for (HttpCookie coo : cookies) {
	            res = coo;
	        }
	    }
	    return res;
	}  
	
	/**
	 * 获取Cookie
	 * @param 	connection
	 * 			连接
	 * @return	Cookie
	 */
	public static String getCookies(HttpURLConnection connection) {
	    String cookieskey = "Set-Cookie";  
	    Map<String, List<String>> maps = connection.getHeaderFields();  
	    List<String> coolist = maps.get(cookieskey);
	    Iterator<String> it = coolist.iterator();
	    StringBuffer sbu = new StringBuffer();
	    sbu.append("eos_style_cookie=default; ");
	    while (it.hasNext()) {
	        sbu.append(it.next());
	    }
	    return sbu.toString();
	}
	
	/**
	 * 读取数据
	 * @param 	request
	 * 			请求
	 * @return	数据
	 * @throws 	Exception
	 */
	public static final byte[] getRequestProtoContent(HttpServletRequest request) throws Exception {
		// get request content length
		final int contentLength = request.getContentLength();
		if (contentLength < 0) {
			throw new Exception("contentLength < 0");
		} else if (contentLength == 0) {
			return NULL_REQUEST;
		} else {
			// get request content
			byte[] data = new byte[contentLength];
			BufferedInputStream bis = new BufferedInputStream(request.getInputStream());
			int readLength = bis.read(data, 0, contentLength);
			
			int count = 0;
			while (readLength < contentLength) {
				// 读取次数超过最大设置读取次数时还没有读取全部请求内容，返回错误
				if ((++count) > CONTENT_MAX_READ_TIMES) {
					throw new Exception();
				}
				readLength += bis.read(data, readLength, contentLength - readLength);
			}
			return data;
		}
	}
	
}
