package gz.jflask.template;

import gz.jflask.FlaskException;
import gz.jflask.InternalServerException;
import gz.jflask.config.Config;
import gz.jflask.config.ConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 6:20 PM
 */
public class TemplateEngines {
    static TemplateEngine templateEngine;
    public static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngines.class);

    public static void init() throws FlaskException {
        Config configs = ConfigHelper.getAppConfigs();
        String engineName = configs.get("template.engine", "default");
        if (engineName.equals("default")) {
            templateEngine = new DefaultTemplateEngine();
        } else {
            try {
                templateEngine = (TemplateEngine) Class.forName(engineName).newInstance();
            } catch (Exception e) {
                throw new InternalServerException(e);
            }
        }
        LOGGER.info("Begin init template engine:" + templateEngine.getName());
        templateEngine.init();
        LOGGER.info("End init template engine:" + templateEngine.getName());
    }

    public static String render(String name, String source, Map<String, ?> context) throws Exception {
        return templateEngine.render(name, source, context);
    }
}
