package simpleHttpServer.reflect;

import simpleHttpServer.exception.SimpleHttpServerException;
import simpleHttpServer.handler.Request;
import simpleHttpServer.handler.Response;
import simpleHttpServer.utils.ConfigSetting;

import java.lang.reflect.Method;

/**
 * @author Mingpoint
 * @ClassName ExecuteMethod
 * @Description TODO
 * @date 2019/5/24 11:54
 **/
public class ExecuteMethod {
    public static void invoke (String key, Request request, Response response) throws Exception {
        String[] split = key.split("#");
        if (split.length < 2) {
            throw new SimpleHttpServerException("没有响应执行类");
        }
        Class<?> aClass = Class.forName(split[0]);
        Object o = Singleton.get(aClass);
        Class<?>[] parameterTypes = {Request.class,Response.class};
        Method method = aClass.getDeclaredMethod(split[1], parameterTypes);
        method.setAccessible(true);
        method.invoke(o,new Object[]{request,response});
    }
}
