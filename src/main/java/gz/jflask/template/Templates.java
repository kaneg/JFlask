package gz.jflask.template;

import gz.jflask.FlaskContext;
import gz.jflask.FlaskException;
import gz.jflask.ResourceHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 7:00 PM
 */
public class Templates {
    public static void process(Template template) throws FlaskException {
        if (template.getType().equalsIgnoreCase("file")) {
            String source = ResourceHelper.getWebInfContent("/views/" + template.getSource());
            if (source != null) {
                render(template.getSource(), source, template.getContext());
            } else {
                throw new FlaskException(500, "No such template:" + template.getSource());
            }
        } else if (template.getType().equalsIgnoreCase("string")) {
            render(null, template.getSource(), template.getContext());
        }
    }

    private static void render(String name, String source, Map<String, ?> context) throws FlaskException {
        HttpServletResponse res = FlaskContext.getResponse();
        try {
            String rendered = TemplateEngines.render(name, source, context);
            if (rendered != null) {
                PrintWriter writer = res.getWriter();
                writer.println(rendered);
                writer.flush();
            }
        } catch (Exception e) {
            throw new FlaskException(500, "Failed to render tpl:" + source, e);
        }
    }
}
