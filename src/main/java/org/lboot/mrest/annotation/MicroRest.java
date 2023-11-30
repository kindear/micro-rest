package org.lboot.mrest.annotation;

import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import org.lboot.mrest.proxy.MicroRestProxy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@ProxyStub(MicroRestProxy.class)
public @interface MicroRest {
    @AliasFor("client")
    String value() default "";

    @AliasFor("value")
    String client() default "";
}
