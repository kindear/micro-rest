package org.lboot.mrest.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Put {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    String[] headers() default {};

    int connectTimeout() default 10;
    int readTimeout() default 10;
    int writeTimeout() default 10;
}
