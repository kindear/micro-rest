package org.lboot.mrest.annotation;

import java.lang.annotation.*;

/**
 * @author kindear 注解属性
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Headers {
    String value() default "";
}
