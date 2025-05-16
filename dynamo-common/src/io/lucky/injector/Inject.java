package io.lucky.injector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, CONSTRUCTOR, PARAMETER, FIELD})
public @interface Inject {

    String value() default "";
}
