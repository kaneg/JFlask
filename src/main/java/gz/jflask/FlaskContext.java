package gz.jflask;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 4:49 PM
 */
public class FlaskContext {
    private static ThreadLocal<WeakReference<RequestResponsePair>> reqRsp = new ThreadLocal<>();
    private static ServletContext servletContext;

    static void setPair(HttpServletRequest request, HttpServletResponse response) {
        RequestResponsePair pair = new RequestResponsePair(request, response);
        WeakReference<RequestResponsePair> wref = new WeakReference<>(pair);
        reqRsp.set(wref);
    }

    public static HttpServletRequest getRequest() {
        return reqRsp.get().get().request;
    }

    public static HttpServletResponse getResponse() {
        return reqRsp.get().get().response;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static void setServletContext(ServletContext servletContext) {
        FlaskContext.servletContext = servletContext;
    }

    private static class RequestResponsePair {
        HttpServletRequest request;
        HttpServletResponse response;

        public RequestResponsePair(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }
    }
}
