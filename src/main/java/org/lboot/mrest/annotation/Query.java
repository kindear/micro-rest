package org.lboot.mrest.annotation;

import java.lang.annotation.*;

/**
 * 请求参数注解
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    String value() default "";
}
