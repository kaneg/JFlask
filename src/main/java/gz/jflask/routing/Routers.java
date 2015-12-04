package gz.jflask.routing;

import gz.jflask.ClassLoaderHelper;
import gz.jflask.FlaskContext;
import gz.jflask.FlaskException;
import gz.jflask.InternalServerException;
import gz.jflask.annotation.App;
import gz.jflask.annotation.Default;
import gz.jflask.annotation.Route;
import gz.jflask.annotation.Var;
import gz.jflask.config.Config;
import gz.jflask.config.ConfigHelper;
import gz.jflask.result.ResultProcessor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 7:34 PM
 */
public class Routers {
    FlaskRouter router;
    ClassLoaderHelper reloadableHelper;

    public void init() throws FlaskException {
        if (reloadableHelper != null) {
            reloadableHelper.close();
        }
        FlaskRouter router = new FlaskRouter();
        List<String> viewClasses = new ArrayList<>();
        Config configs = ConfigHelper.getConfigs("views.conf");
        for (String key : configs.keys()) {
            if (Boolean.parseBoolean(configs.get(key, "false"))) {
                viewClasses.add(key);
            }
        }

        reloadableHelper = new ClassLoaderHelper(viewClasses);
        try {
            for (String className : viewClasses) {
                Class<?> clazz = reloadableHelper.loadClass(className);

                App appView = clazz.getAnnotation(App.class);
                if (appView != null) {
                    Object view = clazz.newInstance();
                    Method[] methods = clazz.getMethods();
                    for (Method m : methods) {
                        Route[] routes = m.getAnnotationsByType(Route.class);
                        for (Route route : routes) {
                            if (route != null) {
                                String[] value = route.value();
                                for (String v : value) {
                                    String[] requestMethods = route.methods();
                                    for (String requestMethod : requestMethods) {
                                        RouteInfo routeInfo = new RouteInfo(requestMethod + ":" + v, m, view);
                                        router.add(requestMethod, v, routeInfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
        this.router = router;
    }

    public void route() throws FlaskException {
        HttpServletRequest req = FlaskContext.getRequest();
        String requestMethod = req.getMethod().toUpperCase();
        FlaskRouter.RuleMatcher ruleMatcher = null;
        try {
            ruleMatcher = router.match(requestMethod, req.getPathInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ruleMatcher == null) {
            throw new FlaskException(404, "No route for path:" + requestMethod + ":" + req.getPathInfo());
        }

        RouteInfo routeInfo = ruleMatcher.getHandler();
        try {
            Method method = routeInfo.getMethod();
            Map<String, Object> variables = ruleMatcher.getVars();
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Var var = parameter.getAnnotation(Var.class);
                Default defaultValue = parameter.getAnnotation(Default.class);
                Object argValue;
                if (variables != null && var != null && variables.containsKey(var.value())) {
                    //get value from var
                    argValue = variables.get(var.value());
                } else if (variables != null && parameter.isNamePresent() && variables.containsKey(parameter.getName())) {
                    //get from parameter name
                    argValue = variables.get(parameter.getName());
                } else if (defaultValue != null) {
                    //get value from default value
                    argValue = defaultValue.value();
                } else {
                    throw new InternalServerException("Invalid route: No var or default value");
                }
                args[i] = convertValueType(argValue, parameter.getType());
            }

            Object result = method.invoke(routeInfo.getView(), args);
            ResultProcessor rp = new ResultProcessor();
            rp.process(result);

        } catch (Exception e) {
            throw new FlaskException(500, "Server Error:" + req.getPathInfo(), e);
        }
    }

    private Object convertValueType(Object value, Class<?> parameterType) {
        if (value.getClass() != parameterType) {
            if (parameterType == int.class || parameterType == Integer.class) {
                return Integer.parseInt(value.toString());
            } else if (parameterType == long.class || parameterType == Long.class) {
                return Long.parseLong(value.toString());
            } else if (parameterType == boolean.class || parameterType == Boolean.class) {
                return Boolean.parseBoolean(value.toString());
            }
        }
        return value;
    }

    public void clear() {
        router.clear();
    }
}
