package net.io.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.rtsp.RtspHeaders.Values;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.text.MessageFormat;
import java.util.Set;

import net.dipatch.ISender;

public class NettyHttpSender implements ISender {
	
	private static final String COOKIE = "eos_style_cookie=default; JSESSIONID={0}; Path=//; HttpOnly";
	
	private final String sessionId;
	
	private final boolean isKeepAlive;
	
	private final Channel channel;
	
	public NettyHttpSender(boolean isKeepAlive, String sessionId, Channel channel) {
		this.isKeepAlive = isKeepAlive;
		this.sessionId = sessionId;
		this.channel = channel;
	}

	@Override
	public void send(byte[] datas, String messageId) throws Exception {
		FullHttpResponse response = createResponse(datas);
		setCookie(response);
		setContent(response);
		channel.writeAndFlush(response);
	}
	
	protected FullHttpResponse createResponse(byte[] datas) {
		return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(datas));
	}
	
	protected void setCookie(FullHttpResponse response) {
		Set<Cookie> cookies = CookieDecoder.decode(MessageFormat.format(COOKIE, sessionId));
		HttpHeaders headers = response.headers();
		for (Cookie cookie : cookies) {
			headers.add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
		}
	}
	
	protected void setContent(FullHttpResponse response) {
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (isKeepAlive) {
            response.headers().set(HttpHeaders.Names.CONNECTION, Values.KEEP_ALIVE);
        }
	}

	@Override
	public <X, Y extends X> void setAttribute(String key, Class<X> clz, Y value) {
		AttributeKey<X> attributeKey = AttributeKey.valueOf(key);
		channel.attr(attributeKey).set(value);
	}

	@Override
	public <X> X getAttribute(String key, Class<X> clz) {
		AttributeKey<X> attributeKey = AttributeKey.valueOf(key);
		Attribute<X> attribute = channel.attr(attributeKey);
		return attribute == null ? null : attribute.get();
	}

}
