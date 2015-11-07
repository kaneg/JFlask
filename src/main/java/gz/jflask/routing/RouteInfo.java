package gz.jflask.routing;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 4:30 PM
 */
public class RouteInfo {
    private final String routePath;
    private final Method method;
    private Object view;

    public RouteInfo(String value, Method m, Object view) {
        this.routePath = value;
        this.method = m;
        this.view = view;
    }

    public String getRoutePath() {
        return routePath;
    }

    public Method getMethod() {
        return method;
    }

    public Object getView() {
        return view;
    }
}
