package simpleHttpServer.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mingpoint
 * @ClassName ConfigSetting
 * @Description 一些常量
 * @date 2019/5/24 9:09
 **/
public class ConfigSetting {
    // controller 映射表
    private static Map<String,String> mapping = new ConcurrentHashMap<>();
//    请求根路径
    private static String rootPath = "";
    private static String scanPackageURL = null;

    public static boolean containsKey (String key) {
        return mapping.containsKey(key);
    }
    public static void put (String key,String value) {
        mapping.put(key, value);
    }
    public static String getValue (String key) {
        return mapping.get(key);
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootPath(String rootPath1) {
        rootPath = rootPath1;
    }

    public static String getScanPackageURL() {
        return scanPackageURL;
    }

    public static void setScanPackageURL(String scanPackageURL1) {
        scanPackageURL = scanPackageURL1;
    }
}
