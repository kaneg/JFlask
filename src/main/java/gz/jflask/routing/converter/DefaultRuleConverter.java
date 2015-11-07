package gz.jflask.routing.converter;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/15/15
 * Time: 10:21 PM
 */
public class DefaultRuleConverter implements RuleConverter {
    @Override
    public String getPattern() {
        return "[^/]+";
    }

    @Override
    public String parse(String str) {
        return str;
    }
}
