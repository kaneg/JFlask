package gz.jflask.result;

import gz.jflask.ResourceHelper;

/**
 * It was Created by kaneg on 7/3/15.
 */
public class StaticResource implements ResponseResult {
    private String path;

    public StaticResource(String path) {
        this.path = path;
    }

    public String toString() {
        return ResourceHelper.streamToString(ResourceHelper.getResourceAsStream(path));
    }
}
