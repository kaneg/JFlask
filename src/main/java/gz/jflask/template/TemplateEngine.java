package gz.jflask.template;

import gz.jflask.FlaskException;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 5:46 PM
 */
public interface TemplateEngine {
    void init() throws FlaskException;

    String render(String name, String content, Map<String, ?> context) throws FlaskException;

    String getName();
}
