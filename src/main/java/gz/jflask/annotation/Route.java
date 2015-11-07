package gz.jflask.annotation;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: kaneg
 * Date: 6/13/15
 * Time: 3:46 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Routes.class)
public @interface Route {
    String[] value();

    String[] methods() default "GET";
}
