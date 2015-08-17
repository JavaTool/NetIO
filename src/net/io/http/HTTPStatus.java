package net.io.http;

/**
 * HTTP状态
 * @author 	fuhuiyuan
 */
public interface HTTPStatus {
	
	/**连接处理成功*/
	int HTTP_STATUS_SUCCESS = 200;
	/**转发*/
	int HTTP_STATUS_REDIRECT = 302;
	/**服务器逻辑错误*/
	int HTTP_STATUS_LOGIC_ERROR = 505;
	/**服务器未处理的错误*/
	int HTTP_STATUS_LOGIC_UNHANLD_EXCEPTION = 500;

}
