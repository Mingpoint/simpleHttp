package simpleHttp.contrllers;

import simpleHttpServer.annotation.SimpleController;
import simpleHttpServer.annotation.SimpleRequestMapping;
import simpleHttpServer.handler.Request;
import simpleHttpServer.handler.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mingpoint
 * @ClassName HelloWorldController
 * @Description TODO
 * @date 2019/5/24 16:12
 **/
@SimpleController
@SimpleRequestMapping("/hello")
public class HelloWorldController {
//    html
    @SimpleRequestMapping("/HTML")
    public void HelloHTML (Request request, Response response) {
        Object id = request.getParam("id");
        System.out.println(id);
        response.setContent("template/TestHTML1111.html");

    }
//    文本
    @SimpleRequestMapping("/Text")
    public void HelloText (Request request, Response response) {
        Object id = request.getParam("id");
        System.out.println(id);
        response.setContentType("text/plain");
        response.setContent("Text");
    }
//    json
    @SimpleRequestMapping("/JSON")
    public void HelloJSON (Request request, Response response) {
        Object id = request.getParam("id");
        System.out.println(id);
        response.setContentType(" application/json");
        Map<String,String> map = new HashMap<>();
        map.put("id1","pox33");
        map.put("id2","pox44");
        map.put("id3","pox55");
        response.setContent(map);
    }
}
