package gz.jflask.routing.converter;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/15/15
 * Time: 10:12 PM
 */
public interface RuleConverter {
    String getPattern();

    Object parse(String str);
}
