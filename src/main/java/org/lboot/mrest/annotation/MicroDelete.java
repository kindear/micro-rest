package org.lboot.mrest.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MicroDelete {
    // 分组名称，不传递则为默认分组
    String groupName() default "";
    String serviceName() default "";
    // 请求路径
    String path() default "";

    String[] headers() default {};

    int connectTimeout() default 10;
    int readTimeout() default 10;
    int writeTimeout() default 10;
}
