package simpleHttpServer.reflect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mingpoint
 * @ClassName Singleton
 * @Description 获取实例
 * @date 2019/5/24 9:54
 **/
public final class Singleton {
    private static Map<Class<?>, Object> pool = new ConcurrentHashMap();
    private Singleton() {
    }

    public static <T> T get(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T obj = (T) pool.get(clazz);
        if (null == obj) {
            synchronized(Singleton.class) {
                obj = (T)pool.get(clazz);
                if (null == obj) {
                    obj = clazz.newInstance();
                    pool.put(clazz, obj);
                }
            }
        }
        return obj;
    }
}
