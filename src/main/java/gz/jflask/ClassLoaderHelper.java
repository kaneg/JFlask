package gz.jflask;

import gz.jflask.config.ConfigHelper;
import gz.jflask.reload.ReloadableClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/23/15
 * Time: 10:27 PM
 */
public class ClassLoaderHelper {
    private ReloadableClassLoader cl;
    private boolean isReload = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderHelper.class);

    public ClassLoaderHelper(List<String> viewClasses) {
        cl = new ReloadableClassLoader(new URL[]{}, viewClasses);
        if (ConfigHelper.getAppConfigs().get("view.auto_reload", "false").equals("true")) {
            String path = ConfigHelper.getAppConfigs().get("view.auto_reload.path");
            LOGGER.info("Auto reload class path:{}", path);
            if (path != null) {
                URL resource = ResourceHelper.getWebInfResourceURL(path);
                cl.addURL(resource);
            }
            Collection<String> classes = ConfigHelper.getConfigs("auto_reload.conf").keys();
            cl.addAutoReloadClasses(classes);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (isReload) {
                        cl.reload();
                        try {
                            Thread.sleep(Integer.parseInt(ConfigHelper.getAppConfigs().get("view.auto_reload.interval", "5")) * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        }
    }

    public Class<?> loadClass(String className) throws FlaskException {
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new InternalServerException(e);
        }
    }

    public void close() {
        isReload = false;
        try {
            cl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
