package gz.jflask.reload;

import gz.jflask.FlaskContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * It was Created by kaneg on 7/5/15.
 */
public class ReloadableClassLoader extends URLClassLoader {
    private Set<String> autoReloadClasses = new HashSet<>();
    private final Map<String, Long> resTimestamp = new HashMap<>();
    private Set<String> alreadyReloaded = new HashSet<>();

    public ReloadableClassLoader(URL[] urls, List<String> viewClasses) {
        super(urls, Thread.currentThread().getContextClassLoader());
        this.autoReloadClasses.addAll(viewClasses);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            if (needReload(name)) {
                alreadyReloaded.add(name);
                return super.findClass(name);
            } else {
                return super.loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            return super.loadClass(name);
        }
    }

    private boolean needReload(String className) {
        for (String name : autoReloadClasses) {
            if (className.startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    public void reload() {
        for (String name : alreadyReloaded) {
            URL resource = findResource(name.replace(".", "/").concat(".class"));
            if (resource == null) {
                continue;
            }
            String file = resource.getFile();
            File f = new File(file);
            long now = f.lastModified();
            Long last = resTimestamp.get(name);
            if (last == null) {
                resTimestamp.put(name, now);
            } else if (now > last) {
                resTimestamp.put(name, now);
                System.out.println("reload:" + name);
                FlaskContext.getServletContext().setAttribute("reload", true);
                break;
            }
        }
    }

    public void addAutoReloadClasses(Collection<String> classes) {
        this.autoReloadClasses.addAll(classes);
    }
}
