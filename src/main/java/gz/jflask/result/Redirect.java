package gz.jflask.result;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 7:16 PM
 */
public class Redirect implements ResponseResult {
    private final String location;

    public Redirect(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
