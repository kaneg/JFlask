package gz.jflask.result;

import gz.jflask.FlaskContext;
import gz.jflask.FlaskException;
import gz.jflask.InternalServerException;
import gz.jflask.template.Template;
import gz.jflask.template.Templates;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 5:35 PM
 */
public class ResultProcessor {
    public void process(Object result) throws FlaskException {
        HttpServletResponse res = FlaskContext.getResponse();
        if (result instanceof String) {
            output((String) result);
        } else if (result instanceof Template) {
            Templates.process((Template) result);
        } else if (result instanceof Redirect) {
            String location = ((Redirect) result).getLocation();
            try {
                res.sendRedirect(location);
            } catch (IOException e) {
                throw new InternalServerException("Failed to redirect to " + location, e);
            }
        } else if (result instanceof StaticResource) {
            StaticResource staticResource = (StaticResource) result;
            output(staticResource.toString());
        } else {
            output(result.toString());
        }
    }

    private void output(String content) throws InternalServerException {
        HttpServletResponse res = FlaskContext.getResponse();
        try {
            PrintWriter writer = res.getWriter();
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new InternalServerException(e);
        }
    }
}
