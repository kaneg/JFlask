package gz.jflask;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/22/15
 * Time: 7:23 PM
 */
public class InternalServerException extends FlaskException {
    public InternalServerException(String message) {
        this(message, null);
    }

    public InternalServerException(Throwable cause) {
        this("", cause);
    }

    public InternalServerException(String message, Throwable cause) {
        super(500, message, cause);
    }
}
