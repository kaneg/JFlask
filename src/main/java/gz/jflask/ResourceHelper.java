package gz.jflask;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 6:39 PM
 */
public class ResourceHelper {
    public static InputStream getResourceAsStream(String path) {
        ServletContext servletContext = FlaskContext.getServletContext();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return servletContext.getResourceAsStream(path);
    }

    public static InputStream getWebInfResource(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return getResourceAsStream("/WEB-INF" + path);
    }

    public static URL getWebInfResourceURL(String path) {
        ServletContext servletContext = FlaskContext.getServletContext();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try {
            return servletContext.getResource("/WEB-INF" + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getWebInfContent(String path) {
        InputStream webInfResource = getWebInfResource(path);
        if (webInfResource != null) {
            return streamToString(webInfResource);

        }
        return null;
    }

    public static String streamToString(InputStream inputStream) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            return new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
