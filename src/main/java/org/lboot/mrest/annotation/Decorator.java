package org.lboot.mrest.annotation;

import com.github.yungyu16.spring.stub.proxy.AbstractInvocationDispatcher;
import org.lboot.mrest.service.ProxyContextDecorator;
import org.lboot.mrest.service.impl.DefaultProxyContextDecorator;

import java.lang.annotation.*;

/**
 * @author kindear
 * 装饰器标识,被该表示标记的,透传上下文信息
 */
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decorator {
    Class<? extends ProxyContextDecorator> value() default DefaultProxyContextDecorator.class;
}
