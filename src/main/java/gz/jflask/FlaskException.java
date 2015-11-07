package gz.jflask;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 4:40 PM
 */
public class FlaskException extends Exception {
    public FlaskException(int statuCode) {
        this(statuCode, null);
    }

    public FlaskException(int statuCode, String message) {
        this(statuCode, message, null);
    }

    public FlaskException(int statuCode, String message, Throwable cause) {
        super(statuCode + ":" + message, cause);
    }
}
