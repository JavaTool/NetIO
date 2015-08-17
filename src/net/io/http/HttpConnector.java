package net.io.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import net.io.util.HttpConnectUtil;

/**
 * HTTP连接器
 * @author	fuhuiyuan
 */
public class HttpConnector extends HttpConnectUtil {
	
	/**Cookie*/
	protected String strcoo;
	/**链接*/
	protected String url;
	
	public HttpConnector(String url) {
		this.url = url;
	}
	
	/**
	 * 连接
	 * @param 	content
	 * 			发送内容
	 * @param 	messageId
	 * 			发送消息id
	 * @return	HTTP反馈信息
	 * @throws 	IOException
	 * @throws 	URISyntaxException
	 */
	public HttpBackInfo connect(byte[] content, String messageId) throws IOException, URISyntaxException {
		HttpURLConnection connection = createConnection(url);
		if (strcoo != null) {
			connection.setRequestProperty("Cookie", "jsessionid=" + strcoo);
		}
		connection.setRequestProperty(HttpConnectUtil.MESSAGEID, messageId);
		HttpBackInfo httpBackInfo = getConnectionStream(connection, content);
		if (strcoo == null) {
			strcoo = getCookies(connection);
//			saveCookies(connection.getURL().toURI(), strcoo);
		}
		return httpBackInfo;
	}
	
	/**
	 * 清空Cookie
	 */
	public void clearCookie() {
		strcoo = null;
	}
	
	public String getCookie() {
		return strcoo;
	}

}
