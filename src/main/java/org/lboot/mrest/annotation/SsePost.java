package org.lboot.mrest.annotation;

import org.lboot.mrest.service.ProxyContextDecorator;
import org.lboot.mrest.service.SseMessageConverter;
import org.lboot.mrest.service.impl.DefaultProxyContextDecorator;
import org.lboot.mrest.service.impl.DefaultSseMessageConverter;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SsePost {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    String[] headers() default {};

    // 终止信号
    String signal() default "DONE";

    Class<? extends SseMessageConverter> converter() default DefaultSseMessageConverter.class;

    int connectTimeout() default 10;
    int readTimeout() default 600;
    int writeTimeout() default 50;
}