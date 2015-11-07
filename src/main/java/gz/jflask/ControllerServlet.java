package gz.jflask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 3:31 PM
 */
public class ControllerServlet extends javax.servlet.http.HttpServlet {
    public static final Logger LOGGER = LoggerFactory.getLogger(ControllerServlet.class);
    private FlaskApp flaskApp = new FlaskApp();

    @Override
    public void init(ServletConfig config) throws ServletException {
        LOGGER.info("Init Controller Servlet...");
        LOGGER.info("Servlet path:{}", config.getServletContext().getRealPath("/"));
        LOGGER.info("Servlet path:{}", config.getServletContext().getRealPath("."));
        try {
            flaskApp.init(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Init Flask failed", e);
        }
        LOGGER.info("Done for init.");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.debug("Serve request");
        try {
            flaskApp.service(req, resp);
        } catch (FlaskException e) {
            throw new ServletException(e);
        }
        LOGGER.debug("End for request");
    }

    @Override
    public void destroy() {
        flaskApp.destroy();
        super.destroy();

    }
}
