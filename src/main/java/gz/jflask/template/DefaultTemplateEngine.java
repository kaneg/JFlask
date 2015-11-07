package gz.jflask.template;

import gz.jflask.FlaskException;
import gz.jflask.InternalServerException;
import gz.jflask.config.ConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 7:56 PM
 */
public class DefaultTemplateEngine implements TemplateEngine {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultTemplateEngine.class);
    private Map<String, CompiledTemplate> cache = new HashMap<>();

    private ScriptEngine python;
    public static final String CREATE_TEMPLATE_FUNCTION = "" +
            "def create_template(source):\n" +
            "    from jinja2 import Environment\n" +
            "    env = Environment()\n" +
            "    tpl = env.from_string(source)\n" +
            "    return tpl";

    @Override
    public String getName() {
        return "Jinja2";
    }

    public void init() throws FlaskException {
        LOGGER.info("Init template engine:{}...", getName());
        try {
            LOGGER.debug("Create new python engine...");
            ScriptEngine python = new ScriptEngineManager().getEngineByName("python");
            if (python == null) {
                throw new InternalServerException("Failed to init python engine");
            }
            LOGGER.debug("Init template function...");
            String jflaskJarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            python.eval("import sys;sys.path.append('" + jflaskJarPath + "/Lib/site-packages')");
            python.eval(CREATE_TEMPLATE_FUNCTION);
            this.python = python;
            LOGGER.debug("Test template function...");
            render(null, "", null);
            LOGGER.info("Init template engine finished.");
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
    }

    @Override
    public String render(String name, String source, Map<String, ?> context) throws FlaskException {
        boolean usCache = ConfigHelper.getAppConfigs().getBoolean("template.use_cache", true);
        CompiledTemplate compiledTemplate = null;
        if (usCache && name != null) {
            compiledTemplate = cache.get(name);
        }
        if (compiledTemplate == null) {
            LOGGER.debug("Create template from ", source);
            compiledTemplate = createTemplate(source);
            if (name != null && usCache) {
                cache.put(name, compiledTemplate);
            }
        }
        return compiledTemplate.render(context);
    }

    private CompiledTemplate createTemplate(String source) throws FlaskException {
        Bindings globalBinding = python.getBindings(ScriptContext.ENGINE_SCOPE);
        Bindings localBinding = python.createBindings();
        localBinding.put("source", source);
        localBinding.put("t", globalBinding.get("create_template"));
        Object o;
        try {
            o = python.eval("t(source)", localBinding);
        } catch (ScriptException e) {
            throw new InternalServerException(e);
        }
        return new CompiledTemplate(python, o);
    }

    static class CompiledTemplate {

        private Object tpl;
        private ScriptEngine python;

        public CompiledTemplate(ScriptEngine python, Object tpl) {
            this.python = python;
            this.tpl = tpl;
        }

        public String render(Map<String, ?> context) throws FlaskException {
            if (context == null) {
                context = new SimpleBindings();
            }

            Bindings localBinding = python.createBindings();
            localBinding.put("tpl", tpl);
            localBinding.put("context", context);
            try {
                return python.eval("tpl.render(context)", localBinding).toString();
            } catch (ScriptException e) {
                throw new InternalServerException(e);
            }
        }
    }
}

