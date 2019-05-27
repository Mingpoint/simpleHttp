package simpleHttpServer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleHttpServer.errors.ErrorController;
import simpleHttpServer.reflect.ExecuteMethod;
import simpleHttpServer.utils.ConfigSetting;

public class ControllerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        final Request request = Request.build(ctx,msg);
        final Response response = Response.build(ctx, request);
        try {
            String uri = msg.uri();
            if (ConfigSetting.containsKey(uri)) {
                String value = ConfigSetting.getValue(uri);
//                反射获取实例化controller,并执行方法
                ExecuteMethod.invoke(value,request,response);
            } else {
                ErrorController.do404Error(request,response);
                return;
            }
        }catch (Exception e) {
            request.setParam("ERROR_NAME",e);
            ErrorController.do500Error(request,response);
            response.send();
            return;
        }
        if (!response.isSend()) {
            response.send();
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        ctx.channel().close();
    }
}
