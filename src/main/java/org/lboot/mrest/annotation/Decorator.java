package org.lboot.mrest.annotation;

import org.lboot.mrest.service.ProxyContextDecorator;
import org.lboot.mrest.service.impl.DefaultProxyContextDecorator;

import java.lang.annotation.*;

/**
 * @author kindear
 * 装饰器标识,被该表示标记的,透传 / 修改上下文信息
 */
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decorator {
    boolean withHeader() default true;

    boolean withBody() default true;
    Class<? extends ProxyContextDecorator> value() default DefaultProxyContextDecorator.class;
}
