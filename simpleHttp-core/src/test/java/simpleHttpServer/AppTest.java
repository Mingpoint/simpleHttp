package simpleHttpServer;
import simpleHttpServer.core.SeverStart;
import simpleHttpServer.utils.ConfigSetting;


/**
 * Unit test for simple App.
 */
public class AppTest {
    public static void main(String[] args) {
        SeverStart severStart = new SeverStart();
        try {
            ConfigSetting.setScanPackageURL("simpleHttpServer.Test");
            severStart.start(8909);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
