package gz.jflask.routing.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/15/15
 * Time: 10:13 PM
 */
public class RuleConverters {
    private static Map<String, RuleConverter> converterMap = new HashMap<>();

    static {
        converterMap.put("path", new PathRuleConverter());
        converterMap.put("int", new IntRuleConverter());
        converterMap.put("default", new DefaultRuleConverter());
    }

    public static RuleConverter getConverter(String converter) {
        if (converter == null) {
            converter = "default";
        }
        return converterMap.get(converter);
    }
}
