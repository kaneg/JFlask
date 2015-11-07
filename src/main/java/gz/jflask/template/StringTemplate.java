package gz.jflask.template;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 6:02 PM
 */
public class StringTemplate implements Template {
    private Map<String, ?> context;
    private String source;

    public StringTemplate(String source) {
        this(source, null);
    }

    public StringTemplate(String source, Map<String, ?> context) {
        this.source = source;
        this.context = context;
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Map<String, ?> getContext() {
        return context;
    }
}
