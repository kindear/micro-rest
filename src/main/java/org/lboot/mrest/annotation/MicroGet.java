package org.lboot.mrest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MicroGet {
    String serviceName() default "";
    // 请求路径
    String path() default "";

    String[] headers() default {};
}
