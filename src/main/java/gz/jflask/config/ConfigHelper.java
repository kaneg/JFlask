package gz.jflask.config;

import gz.jflask.InternalServerException;
import gz.jflask.ResourceHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 7:48 PM
 */
public class ConfigHelper {
    public static Config APP_CONFIG;

    public static Config getConfigs(String path) {
        Config config = new Config();
        InputStream res = ResourceHelper.getWebInfResource(path);
        if (res == null) {
            return config;
        }
        Properties p = new Properties();

        try {
            p.load(res);
        } catch (IOException e) {
            throw new RuntimeException(new InternalServerException(e));
        }
        @SuppressWarnings("unchecked")
        Enumeration<String> enumeration = (Enumeration<String>) p.propertyNames();
        while (enumeration.hasMoreElements()) {
            String s = enumeration.nextElement();
            config.put(s, p.getProperty(s));
        }
        return config;
    }

    public synchronized static Config getAppConfigs() {
        if (APP_CONFIG == null) {
            APP_CONFIG = getConfigs("app.conf");
        }
        return APP_CONFIG;
    }
}
