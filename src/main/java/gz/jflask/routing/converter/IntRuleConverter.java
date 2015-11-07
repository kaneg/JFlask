package gz.jflask.routing.converter;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/15/15
 * Time: 10:21 PM
 */
public class IntRuleConverter implements RuleConverter {
    @Override
    public String getPattern() {
        return "\\d+";
    }

    @Override
    public Integer parse(String str) {
        return Integer.parseInt(str);
    }
}
