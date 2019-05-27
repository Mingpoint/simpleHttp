package simpleHttp;

import simpleHttpServer.core.SeverStart;
import simpleHttpServer.utils.ConfigSetting;

public class App {
    public static void main(String[] args) {
        SeverStart start = new SeverStart();
        try {
//            设置包扫描路径
            ConfigSetting.setScanPackageURL("simpleHttp.contrllers");
//            设置访问路径，默认是/
            ConfigSetting.setRootPath("/example");

            start.start(8090);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
