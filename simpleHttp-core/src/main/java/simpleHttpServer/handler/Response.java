package simpleHttpServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import simpleHttpServer.exception.SimpleHttpServerException;
import simpleHttpServer.reflect.ClassUtil;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 *@ClassName Response
 *@Description 请求响应类
 *@author Mingpoint
 *@date 2019/5/23 11:10
 **/

public class Response extends AbstractResponse{
    private Map<String,Object> headers = new HashMap<>();
    private Set<Cookie> cookies = new HashSet<>();
    private ChannelHandlerContext ctx;
    private Request request;

    private Response(ChannelHandlerContext ctx,Request request) {
        this.ctx = ctx;
        this.request = request;
    }

    /**
     * @description 构建响应
     * @author: Mingpoint
     * @date 2019/5/23 11:37
    **/
    public final static Response build (ChannelHandlerContext ctx,Request request) {
        return new Response(ctx,request);
    }
    /**
     * @description 设置响应头
     * @author: Mingpoint
     * @date 2019/5/23 11:37
    **/
    public void setHeader (String name,Object value) {
        this.headers.put(name,value);
    }
    /**
     * @description 设置cookie
     * @author: Mingpoint
     * @date 2019/5/23 11:38
    **/
    public void setCookie (Cookie cookie) {
        cookies.add(cookie);
    }
    public void setCookie (String name,String value) {
        Cookie cookie = new DefaultCookie(name,value);
        cookies.add(cookie);
    }
    /**
     * @description 设置cookie
     * @author: Mingpoint
     * @date 2019/5/23 11:38
    **/
    public void setCookie (String name, String value, int maxAge, String path, String domain) {
        Cookie cookie = new DefaultCookie(name,value);
        cookie.setDomain(domain);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookies.add(cookie);
    }

    /**
     * @description 设置响应参数
     * @author: Mingpoint
     * @date 2019/5/23 14:37
    **/
    public FullHttpResponse response ()  {
        ByteBuf byteBuf = Unpooled.EMPTY_BUFFER;
        if ("text/html".equals(this.getContentType())) {
            byteBuf = getByteBuf();
        } else {
            byteBuf = Unpooled.copiedBuffer(this.getContent().toString(), Charset.forName(this.getCharset()));
        }
        DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, this.getStatus(),byteBuf);
//        响应头设置
        HttpHeaders headers = fullHttpResponse.headers();
        headers.add(new DefaultHttpHeaders());
        headers.set(HttpHeaderNames.CONTENT_TYPE.toString(), this.getContentType());
        headers.set(HttpHeaderNames.CONTENT_LENGTH.toString(), byteBuf.readableBytes());
        headers.set(HttpHeaderNames.CONTENT_ENCODING.toString(), this.getCharset());
        headers.set(HttpHeaderNames.DATE.toString(), new Date());
        headers.set(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString());
//        cookie 设置
        for (Cookie cookie : cookies) {
            headers.set(HttpHeaderNames.COOKIE.toString(), ServerCookieEncoder.LAX.encode(cookie));
        }
        return fullHttpResponse;
    }
    /**
     * @description 发送响应
     * @author: Mingpoint
     * @date 2019/5/23 14:36
    **/
    protected ChannelFuture send () {
        FullHttpResponse response = null;
        if (this.getContent() instanceof File) {
//文件类型
        } else {
            if (null != request && request.isKeepAlive()) {
                response = this.response();
            } else {
               setSend(true);
               return ctx.writeAndFlush(this.response()).addListener(ChannelFutureListener.CLOSE);
            }
        }
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        setSend(true);
        return channelFuture;
    }
    public void sendError(HttpResponseStatus status, String msg) {
        if (ctx.channel().isActive()) {
            this.setStatus(status);
            this.setContent(msg);
            this.send();
        } else {
            throw new SimpleHttpServerException("channel异常");
        }
    }
    private ByteBuf getByteBuf () {
//        ByteBuf byteBuf = Unpooled.copiedBuffer();
        ByteBuf byteBuf = null;
        try {
            String s = this.getContent().toString();
            URL url = ClassUtil.getClassLoader().getResource(s);
            File f = new File(url.getPath());
            InputStream inputStream = new FileInputStream(f);
            byteBuf = Unpooled.buffer(inputStream.available());
            byteBuf.writeBytes(inputStream, inputStream.available());
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteBuf;
    }
}
