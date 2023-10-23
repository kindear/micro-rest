package org.lboot.mrest.annotation;

import java.lang.annotation.*;

/**
 * 应用于 POST 请求携带参数 body 请求体构建
 */
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
    String value() default "";
}
