package gz.jflask;

import gz.jflask.routing.Routers;
import gz.jflask.template.TemplateEngines;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 3:39 PM
 */
public class FlaskApp {
    Routers routers = new Routers();

    public void service(HttpServletRequest req, HttpServletResponse resp) throws FlaskException {
        FlaskContext.setPair(req, resp);
        Object reload = FlaskContext.getServletContext().getAttribute("reload");
        boolean needReload = false;
        if (reload != null && (Boolean) reload) {
            needReload = true;
            FlaskContext.getServletContext().removeAttribute("reload");
        }
        if (req.getPathInfo().equals("/reload")) {
            try {
                routers.init();
                resp.getWriter().println("Reloaded");
                return;
            } catch (Exception e) {
                throw new InternalServerException("Initiate route failed", e);
            }
        }
        if (needReload) {
            routers.init();
        }
        routers.route();
    }

    public void init(ServletConfig config) throws Exception {
        ServletContext servletContext = config.getServletContext();
        FlaskContext.setServletContext(servletContext);
        routers.init();
        TemplateEngines.init();
    }

    public void destroy() {
        routers.clear();
    }
}
