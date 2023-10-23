package org.lboot.mrest.annotation;

import java.lang.annotation.*;

/**
 * @author kindear
 * 路径变量
 */
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVar {
    String value() default "";
}
