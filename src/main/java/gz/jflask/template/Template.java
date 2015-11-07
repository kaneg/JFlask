package gz.jflask.template;

import gz.jflask.result.ResponseResult;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 5:55 PM
 */
public interface Template extends ResponseResult {
    String getType();

    String getSource();

    Map<String, ?> getContext();
}