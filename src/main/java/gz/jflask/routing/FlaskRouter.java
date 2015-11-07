package gz.jflask.routing;

import gz.jflask.routing.converter.RuleConverter;
import gz.jflask.routing.converter.RuleConverters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 5:43 PM
 */
public class FlaskRouter {
    private static final String PS = "(?<static>[^<]*)                      # static rule data\n" +
            "<\n" +
            "(?:\n" +
            "        (?<converter>[a-zA-Z_][a-zA-Z0-9_]*)     # converter name\n" +
            "        (?:\\((?<args>.*?)\\))?                  # converter arguments\n" +
            "        \\:                                      # variable delimiter\n" +
            "    )?\n" +
            "(?<variable>[a-zA-Z_][a-zA-Z0-9_]*) \n" +
            ">";
    private static final Pattern URL_RULE = Pattern.compile(PS, Pattern.COMMENTS);

    Map<String, Map<String, RouteInfo>> staticRouters = new HashMap<>();
    Map<String, Map<Pattern, RouteRule>> dynamicRouters = new HashMap<>();

    private Map<String, RouteInfo> getStaticRouterMap(String method) {
        Map<String, RouteInfo> routerMap = staticRouters.get(method);
        if (routerMap == null) {
            routerMap = new HashMap<>();
        }
        staticRouters.put(method, routerMap);
        return routerMap;
    }

    private Map<Pattern, RouteRule> getDynamicRouterMap(String method) {
        Map<Pattern, RouteRule> routerMap = dynamicRouters.get(method);
        if (routerMap == null) {
            routerMap = new HashMap<>();
        }
        dynamicRouters.put(method, routerMap);
        return routerMap;
    }

    public void add(String method, String rule, RouteInfo handler) {
        Map<String, RouteInfo> staticRoutersMap = getStaticRouterMap(method);
        Map<Pattern, RouteRule> dynamicRoutersMap = getDynamicRouterMap(method);
        if (!rule.contains("<")) {
            staticRoutersMap.put(rule, handler);
        } else {
            RouteRule dynamicRule = getDynamicRule(rule);
            String regex = dynamicRule.getRegex();
            dynamicRule.setHandler(handler);
            dynamicRoutersMap.put(Pattern.compile(regex), dynamicRule);
        }
    }

    private RouteRule getDynamicRule(String rule) {
        List<RulePart> ruleParts = getRuleParts(rule);
        StringBuilder sb = new StringBuilder();
        for (RulePart rp : ruleParts) {
            sb.append(rp.staticPart);
            RuleConverter converter = RuleConverters.getConverter(rp.converter);
            String pattern = converter.getPattern();
            sb.append(String.format("(?<%s>%s)", rp.variable, pattern));
        }
        return new RouteRule(sb.toString(), ruleParts);
    }

    private List<RulePart> getRuleParts(String rule) {
        Matcher m = URL_RULE.matcher(rule);

        List<RulePart> ruleParts = new ArrayList<>();
        int start = 0;
        while (m.find(start)) {
            start = m.end();
            ruleParts.add(new RulePart(m.group("static"), m.group("variable"), m.group("converter"), m.group("args")));
        }
        return ruleParts;
    }

    public RuleMatcher match(String method, String path) throws Exception {
        Map<String, RouteInfo> methodRouters = staticRouters.get(method);
        if (methodRouters != null) {
            for (String rule : methodRouters.keySet()) {
                if (rule.equals(path)) {
                    return new RuleMatcher(methodRouters.get(rule));
                }
            }
        }

        Map<Pattern, RouteRule> patternObjectMap = dynamicRouters.get(method);
        if (patternObjectMap != null) {
            for (Pattern pattern : patternObjectMap.keySet()) {
                Map<String, Integer> groupMap = getNameGroupMap(pattern);
                Matcher matcher = pattern.matcher(path);
                Map<String, Object> vars = new HashMap<>();
                if (matcher.matches()) {
                    RouteRule routeRule = patternObjectMap.get(pattern);
                    for (String varName : groupMap.keySet()) {
                        String groupValue = matcher.group(groupMap.get(varName));

                        vars.put(varName, routeRule.getConverter(varName).parse(groupValue));
                    }
                    return new RuleMatcher(routeRule.getHandler(), vars);
                }
            }
        }
        return null;
    }

    private Map<String, Integer> getNameGroupMap(Pattern rule) throws Exception {
        Method namedGroups = Pattern.class.getDeclaredMethod("namedGroups");
        namedGroups.setAccessible(true);
        return (Map<String, Integer>) namedGroups.invoke(rule);
    }

    public void clear() {
        staticRouters.clear();
        dynamicRouters.clear();
    }

    static class RulePart {
        String staticPart;
        String variable;
        String converter;
        String args;

        public RulePart(String staticPart, String variable, String converter, String args) {
            this.staticPart = staticPart;
            this.variable = variable;
            this.converter = converter;
            this.args = args;
        }
    }

    public static class RuleMatcher {
        private RouteInfo handler;
        private Map<String, Object> vars;

        public RuleMatcher(RouteInfo handler) {
            this.handler = handler;
        }

        public RuleMatcher(RouteInfo handler, Map<String, Object> vars) {
            this.handler = handler;
            this.vars = vars;
        }

        public RouteInfo getHandler() {
            return handler;
        }

        public Map<String, Object> getVars() {
            return vars;
        }
    }
}
