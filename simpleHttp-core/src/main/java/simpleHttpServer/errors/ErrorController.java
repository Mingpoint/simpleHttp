package simpleHttpServer.errors;

import io.netty.handler.codec.http.HttpResponseStatus;
import simpleHttpServer.handler.Request;
import simpleHttpServer.handler.Response;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Mingpoint
 * @ClassName ErrorController
 * @Description TODO
 * @date 2019/5/24 10:35
 **/
public class ErrorController {
    private final static String TEMPLATE_ERROR = "<!DOCTYPE html><html><head><title>simpleHttpServer - Error report</title><style>h1,h3 {color:white; background-color: gray;}</style></head><body><h1>HTTP Status %s - %s </h1><hr size=\"1\" noshade=\"noshade\" /><p>%s</p><hr size=\"1\" noshade=\"noshade\" /><h3>simpleHttpServer</h3></body></html>";

    public static void do404Error (Request request, Response response) {
        String str = String.format(TEMPLATE_ERROR, "404", request.getUrl(), "404 File not found!");
        response.sendError(HttpResponseStatus.NOT_FOUND, str);
        return;
    }
    public static void do500Error (Request request, Response response) {
        Object obj = request.getParam("ERROR_NAME");
        if (obj instanceof Exception) {
            Exception e = (Exception)obj;
            final StringWriter writer = new StringWriter();
            // 把错误堆栈储存到流中
            e.printStackTrace(new PrintWriter(writer));
            String content = writer.toString().replace("\tat", "&nbsp;&nbsp;&nbsp;&nbsp;\tat");
            content = content.replace("\n", "<br/>\n");
            content = String.format(TEMPLATE_ERROR, "500", request.getUrl(), content);
            response.sendError(HttpResponseStatus.INTERNAL_SERVER_ERROR, content);
        }
    }
}
