package gz.jflask.template;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 6:02 PM
 */
public class FileTemplate extends StringTemplate {

    public FileTemplate(String tplName) {
        super(tplName);
    }

    public FileTemplate(String tplName, Map<String, ?> context) {
        super(tplName, context);
    }

    @Override
    public String getType() {
        return "file";
    }
}
