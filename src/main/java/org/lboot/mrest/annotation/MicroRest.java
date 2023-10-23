package org.lboot.mrest.annotation;

import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import org.lboot.mrest.proxy.MicroRestProxy;

import java.lang.annotation.*;
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@ProxyStub(MicroRestProxy.class)
public @interface MicroRest {
    
}
