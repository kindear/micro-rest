package org.lboot.mrest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MicroPut {
    // 分组名称，不传递则为默认分组
    String groupName() default "";
    // 微服务名称
    String serviceName() default "";

    // 请求路径
    String path() default "";

    // 请求头信息
    String[] headers() default {};

    int connectTimeout() default 10;
    int readTimeout() default 10;
    int writeTimeout() default 10;

}
