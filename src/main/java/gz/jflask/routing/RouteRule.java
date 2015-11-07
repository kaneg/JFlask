package gz.jflask.routing;

import gz.jflask.routing.converter.RuleConverter;
import gz.jflask.routing.converter.RuleConverters;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/16/15
 * Time: 10:41 PM
 */
public class RouteRule {
    private final String regex;
    private final List<FlaskRouter.RulePart> ruleParts;
    private RouteInfo handler;

    public RouteRule(String regex, List<FlaskRouter.RulePart> ruleParts) {
        this.regex = regex;
        this.ruleParts = ruleParts;
    }

    public String getRegex() {
        return regex;
    }

    public List<FlaskRouter.RulePart> getRuleParts() {
        return ruleParts;
    }

    public RouteInfo getHandler() {
        return handler;
    }

    public void setHandler(RouteInfo handler) {
        this.handler = handler;
    }

    public RuleConverter getConverter(String varName) {
        for (FlaskRouter.RulePart rp : ruleParts) {
            if (varName.equals(rp.variable)) {
                return RuleConverters.getConverter(rp.converter);
            }
        }
        return null;
    }
}
