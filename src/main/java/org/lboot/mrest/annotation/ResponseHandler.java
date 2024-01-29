package org.lboot.mrest.annotation;

import org.lboot.mrest.service.ProxyContextDecorator;
import org.lboot.mrest.service.ProxyResponseHandler;
import org.lboot.mrest.service.impl.DefaultProxyContextDecorator;

import java.lang.annotation.*;

/**
 * @author kindear
 * 请求响应处理器
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseHandler {
    Class<? extends ProxyResponseHandler> value() default ProxyResponseHandler.class;
}
