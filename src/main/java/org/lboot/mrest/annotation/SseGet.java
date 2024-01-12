package org.lboot.mrest.annotation;

import org.lboot.mrest.service.SseMessageConverter;
import org.lboot.mrest.service.impl.DefaultSseMessageConverter;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * SSE Get
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SseGet {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    String[] headers() default {};

    // 终止信号
    String signal() default "DONE";


    Class<? extends SseMessageConverter> converter() default DefaultSseMessageConverter.class;

}
