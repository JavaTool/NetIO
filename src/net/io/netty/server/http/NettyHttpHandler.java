package net.io.netty.server.http;

import java.text.MessageFormat;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.content.Content;
import net.content.IContent;
import net.content.IContentFactory;
import net.dipatch.IDispatchManager;
import net.io.ISender;
import net.io.java.IHttpSession;

public class NettyHttpHandler extends SimpleChannelInboundHandler<Object> {
	
	private static final Logger log = LoggerFactory.getLogger(NettyHttpHandler.class);
	
	private static final AttributeKey<NettyHttpSession> SESSION = AttributeKey.valueOf("NettyHttpSession");
	
	private static final String COOKIE = "Cookie";
	
	private static final String COOKIE_FORMT = "eos_style_cookie=default; JSESSIONID={0}; Path=//; HttpOnly";
	
	private static final String CONTENT_LENGHT = "Content-Length";
	
	private static final String MESSAGE_ID = "messageId";
	
	private final IDispatchManager dispatchManager;
	
	private final IContentFactory nettyContentFactory;
	
	public NettyHttpHandler(IDispatchManager dispatchManager, IContentFactory nettyContentFactory) {
		this.dispatchManager = dispatchManager;
		this.nettyContentFactory = nettyContentFactory;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel channel = ctx.channel();
		if (msg instanceof HttpRequest) { // http请求头
			readHead(channel, (HttpRequest) msg);
		} else if (msg instanceof HttpContent) { // http请求体
			HttpContent httpContent = (HttpContent) msg;
			Attribute<NettyHttpSession> session = channel.attr(SESSION);
			NettyHttpSession httpSession = session.get();
			if (httpSession != null) {
				ByteBuf content = httpContent.content();
				httpSession.read(content);
				if (httpContent instanceof LastHttpContent) {
					byte[] datas = httpSession.datas;
					httpSession.reset();
					readContent(channel, httpSession, datas);
				}
			}
		}
	}
	
	private void readHead(Channel channel, HttpRequest req) {
		HttpHeaders headers = req.headers();
		String cookie = headers.get(COOKIE);
		if (cookie != null) {
			cookie = cookie.replaceFirst("JSESSIONID", "jsessionid");
		}
		Attribute<NettyHttpSession> session = channel.attr(SESSION);
		NettyHttpSession httpSession = session.get();
		if (httpSession == null) {
			String sessionId = cookie == null || cookie.length() == 0 ? MessageFormat.format(COOKIE_FORMT, UUID.randomUUID().toString()) : cookie;
			httpSession = new NettyHttpSession(sessionId, HttpHeaders.isKeepAlive(req), channel);
			session.set(httpSession);
		}
		httpSession.setMessageId(Integer.parseInt(headers.get(MESSAGE_ID)));
		String contentLenght = headers.get(CONTENT_LENGHT);
		httpSession.setContentLength(contentLenght == null || contentLenght.length() == 0 ? 0 : Integer.parseInt(contentLenght));
	}
	
	private void readContent(Channel channel, NettyHttpSession httpSession,  byte[] datas) {
		IContent content = nettyContentFactory.createContent(datas, httpSession);
//		log.info("addDispatch IP : {} sessionId : {}", channel.remoteAddress(), content.getSessionId());
		dispatchManager.addDispatch(content);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		log.info("[Http Coming Out]IP:{}", channel.remoteAddress());
		Attribute<NettyHttpSession> session = channel.attr(SESSION);
		NettyHttpSession httpSession = session.get();
		if (httpSession != null) {
			String sessionId = httpSession.getId();
			dispatchManager.disconnect(new Content(sessionId, 0, null, new NettyHttpSender(true, sessionId, channel)));
		}
		channel.close();
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.info("[Http Coming Error]IP:{} ; Error:{}", ctx.channel().remoteAddress(), cause.getLocalizedMessage());
		ctx.channel().close();
		ctx.close();
		if (!cause.getLocalizedMessage().contains("远程主机强迫关闭了一个现有的连接。")) {
			cause.printStackTrace();
		}
	}
	
	private static class NettyHttpSession implements IHttpSession {
		
		private int contentLength;
		
		private int messageId;
		
		private byte[] datas;
		
		private final String id;
		
		private final boolean isKeepAlive;
		
		private final ISender sender;
		
		public NettyHttpSession(String id, boolean isKeepAlive, Channel channel) {
			id = id.replace(" ", "");
			this.id = id;
			this.isKeepAlive = isKeepAlive;
			sender = new NettyHttpSender(isKeepAlive, id, channel);
		}

		@Override
		public int getContentLength() {
			return contentLength;
		}

		public void setContentLength(int contentLength) {
			this.contentLength = contentLength;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public int getMessageId() {
			return messageId;
		}

		public void setMessageId(int messageId) {
			this.messageId = messageId;
		}

		@Override
		public boolean isKeepAlive() {
			return isKeepAlive;
		}

		@Override
		public ISender getSender() {
			return sender;
		}
		
		public void read(ByteBuf content) {
			int capacity = content.capacity();
			byte[] datas = new byte[capacity];
			content.readBytes(datas);
			if (this.datas == null) {
				this.datas = datas;
			} else {
				this.datas = appendArray(this.datas, datas);
			}
		}
		
		public void reset() {
			datas = null;
		}
		
	}
	
	private static byte[] appendArray(byte[] array1, byte[] array2) {
		int length1 = array1.length;
		int length2 = array2.length;
		byte[] array = new byte[length1 + length2];
		System.arraycopy(array1, 0, array, 0, length1);
		System.arraycopy(array2, 0, array, length1, length2);
		return array;
	}

}
