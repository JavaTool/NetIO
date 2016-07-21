package net.io.http.client;

import static java.net.URLEncoder.encode;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

/**
 * HTTP连接器
 * @author 	fuhuiyuan
 * @param 	<T>
 * 			请求返回对象类型
 */
public interface IHttpConnector<T> {
	
	/** 空byte数组，用来填充可能为空的参数 */
	byte[] EMPTY_BYTES = new byte[0];
	/** 空参数字符串，用来填充可能为空的参数 */
	String EMPTY_PARAMS = "";
	/** 空头信息集合，用来填充可能为空的参数 */
	Map<String, Object> EMPTY_HEAD = ImmutableMap.of();
	
	/**
	 * 设置连接地址
	 * @param 	url
	 * 			地址
	 */
	void setUrl(String url);
	/**
	 * 设置响应码处理器
	 * @param 	handler
	 * 			响应码处理器
	 */
	void setResponseCodeHandler(IResponseCodeHandler handler);
	/**
	 * 获取Cookie
	 * @return	
	 */
	String getCookie();
	
	// POST
	
	T post(String params, byte[] bytes, Map<String, Object> head) throws Exception;
	
	default T post(String params, Map<String, Object> head) throws Exception {
		return post(params, EMPTY_BYTES, EMPTY_HEAD);
	}
	
	default T post(String params, byte[] bytes) throws Exception {
		return post(params, bytes, EMPTY_HEAD);
	}
	
	default T post(byte[] bytes, Map<String, Object> head) throws Exception {
		return post(EMPTY_PARAMS, bytes, head);
	}
	
	default T post(String params) throws Exception {
		return post(params, EMPTY_HEAD);
	}
	
	default T post(byte[] bytes) throws Exception {
		return post(EMPTY_PARAMS, bytes, EMPTY_HEAD);
	}
	
	default T post(Map<String, Object> params, byte[] bytes, Map<String, Object> head) throws Exception {
		return post(makeParams(params), bytes, head);
	}
	
	default T post(Map<String, Object> params, Map<String, Object> head) throws Exception {
		return post(makeParams(params), head);
	}
	
	default T post(Map<String, Object> params) throws Exception {
		return post(params, EMPTY_HEAD);
	}
	
	default T post() throws Exception {
		return post(EMPTY_PARAMS, EMPTY_BYTES, EMPTY_HEAD);
	}
	
	// GET
	
	T get(String params, Map<String, Object> head) throws Exception;
	
	default T get(String params) throws Exception {
		return get(params, EMPTY_HEAD);
	}
	
	default T get(Map<String, Object> params, Map<String, Object> head) throws Exception {
		return get(makeParams(params), head);
	}
	
	default T get(Map<String, Object> params) throws Exception {
		return get(params, EMPTY_HEAD);
	}
	
	default T get() throws Exception {
		return get(EMPTY_PARAMS, EMPTY_HEAD);
	}
	
	// PUT
	
	T put(String params, byte[] bytes, Map<String, Object> head) throws Exception;
	
	default T put(String params, Map<String, Object> head) throws Exception {
		return put(params, EMPTY_BYTES, head);
	}
	
	default T put(String params, byte[] bytes) throws Exception {
		return put(params, bytes, EMPTY_HEAD);
	}
	
	default T put(byte[] bytes, Map<String, Object> head) throws Exception {
		return put(EMPTY_PARAMS, bytes, head);
	}
	
	default T put(String params) throws Exception {
		return put(params, EMPTY_HEAD);
	}
	
	default T put(byte[] bytes) throws Exception {
		return put(EMPTY_PARAMS, bytes, EMPTY_HEAD);
	}
	
	default T put(Map<String, Object> params, Map<String, Object> head) throws Exception {
		return put(makeParams(params), head);
	}
	
	default T put(Map<String, Object> params, byte[] bytes, Map<String, Object> head) throws Exception {
		return put(makeParams(params), bytes, head);
	}
	
	default T put(Map<String, Object> params, byte[] bytes) throws Exception {
		return put(makeParams(params), bytes, EMPTY_HEAD);
	}
	
	default T put(Map<String, Object> params) throws Exception {
		return put(params, EMPTY_HEAD);
	}
	
	default T put() throws Exception {
		return put(EMPTY_PARAMS, EMPTY_HEAD);
	}
	
	// DELETE
	
	T delete(String params, Map<String, Object> head) throws Exception;
	
	default T delete(String params) throws Exception {
		return delete(params, EMPTY_HEAD);
	}
	
	default T delete(Map<String, Object> params, Map<String, Object> head) throws Exception {
		return delete(makeParams(params), head);
	}
	
	default T delete(Map<String, Object> params) throws Exception {
		return delete(params, EMPTY_HEAD);
	}
	
	default T delete() throws Exception {
		return delete(EMPTY_PARAMS, EMPTY_HEAD);
	}
	
	
	public static String makeParams(Map<String, Object> params) throws IOException {
		StringBuilder builder = new StringBuilder("?");
		for (Entry<String, Object> entry : params.entrySet()) {
			builder.append(entry.getKey()).append("=").append(encode(String.valueOf(entry.getValue()), "utf-8")).append("&");
		}
		builder.setLength(builder.length() - 1);
		return builder.toString();
	}
	
	public static interface IResponseCodeHandler {
		
		void handleResponseCode(int code) throws Exception;
		
	}

}
