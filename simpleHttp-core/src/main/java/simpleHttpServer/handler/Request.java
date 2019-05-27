package simpleHttpServer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Request {
    Logger logger = LoggerFactory.getLogger(Request.class);
    private FullHttpRequest nettyFullRequest;
    private String url;
    private String ip;
    private Map<String,String> headers = new HashMap<>();
    private Map<String,Object> params = new HashMap<>();
    private Map<String,Cookie> cookies = new HashMap<>();
    private Request (ChannelHandlerContext ctx,FullHttpRequest nettyFullRequest) {

        this.nettyFullRequest = nettyFullRequest;
        setUrl(nettyFullRequest.uri());
        setHeaders(nettyFullRequest.headers());
        setCookies();
        putParams(new QueryStringDecoder(url));

        if (nettyFullRequest.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = null;
            try {
                decoder = new HttpPostRequestDecoder(nettyFullRequest);
                putParams(decoder);
            }finally {
                if (null != decoder) {
                    decoder.destroy();
                }
            }
        }
        setIp(ctx);
    }

    public void setIp (ChannelHandlerContext ctx) {
        String ip = this.headers.get("X-Forwarded-For");
        if (StringUtil.isNullOrEmpty(ip)) {
            final InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
            ip = socketAddress.getAddress().getHostAddress();
        }
        this.ip = ip;

    }
     /** 设置POST参数
       * @description
       * @author: Mingpoint
       * @date 2019/5/23 9:22
       **/
    protected void putParams(HttpPostRequestDecoder decoder) {
        if (null == decoder) {
            return;
        }
        for (InterfaceHttpData data :decoder.getBodyHttpDatas()) {
            InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
//            普通参数
            if (dataType == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) data;
                try {
                    this.params.put(attribute.getName(),attribute.getValue());
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
//            文件类型参数
            if (dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                FileUpload fileUpload = (FileUpload) data;
                if (fileUpload.isCompleted()) {
                    try {
                        this.params.put(fileUpload.getFilename(),fileUpload.getFile());
                    } catch (IOException e) {
                        logger.error("get file param "+data.getName()+"fail",e);
                    }
                }
            }
        }

    }
     /**
       * @description 设置GET请求参数
       * @author: Mingpoint
       * @date 2019/5/23 9:12
       **/
    protected void putParams(QueryStringDecoder decoder) {
        if (null !=  decoder) {
            List<String> list = null;
            for (Map.Entry<String,List<String>> entry : decoder.parameters().entrySet()) {
                list = entry.getValue();
                if (null != list) {
                    this.params.put(entry.getKey(),list);
                }
            }
        }
    }
    public void getIp () {

    }

    /**
     * @description 设置请求头
     * @author: Mingpoint
     * @date 2019/5/22 16:49
     **/

    public void setHeaders (HttpHeaders headers) {
        for(Map.Entry<String,String> entry : headers.entries()) {
            this.headers.put(entry.getKey(),entry.getValue());
        }
    }

      /**
       * @description 设置cookies
       * @author: Mingpoint
       * @date 2019/5/22 16:49
       **/
    public void setCookies () {
        String cookieString = this.headers.get(HttpHeaderNames.COOKIE.toString());
        if (!StringUtil.isNullOrEmpty(cookieString)) {
            Set<Cookie> cookies = ServerCookieDecoder.LAX.decode(cookieString);
            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.name(),cookie);
            }
        }
    }
    /**
     * @description 是否为长链接
     * @author: Mingpoint
     * @date 2019/5/23 14:22
    **/
    public boolean isKeepAlive() {
        String connetion = this.headers.get(HttpHeaderNames.CONNECTION.toString());
        if (HttpHeaderValues.CLOSE.toString().equalsIgnoreCase(connetion)) {
            return false;
        }
        if (HttpVersion.HTTP_1_0.equals(nettyFullRequest.protocolVersion().text())) {
            return HttpHeaderValues.KEEP_ALIVE.toString().equalsIgnoreCase(connetion);
        }
        return true;
    }

    protected final static Request build(ChannelHandlerContext ctx,FullHttpRequest nettyFullRequest) {
        return  new Request(ctx,nettyFullRequest);
    }
    public Object getParam (String key) {
        return this.params.get(key);
    }
    public void setParam (String key,Object obj) {
        this.params.put(key,obj);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
