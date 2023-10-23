package org.lboot.mrest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MicroPost {
    // 微服务名称
    String serviceName() default "";

    // 请求路径
    String path() default "";

    // 请求头信息
    String[] headers() default {};

}
