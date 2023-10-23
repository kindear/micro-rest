package org.lboot.mrest.proxy;

import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import com.github.yungyu16.spring.stub.proxy.AbstractInvocationDispatcher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.annotation.Get;
import org.lboot.mrest.annotation.MicroGet;
import org.lboot.mrest.annotation.MicroPost;
import org.lboot.mrest.annotation.Post;
import org.lboot.mrest.handler.GetRequestHandler;
import org.lboot.mrest.handler.MicroPostRequestHandler;
import org.lboot.mrest.handler.PostRequestHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author kindear
 * 抽象服务实现
 * MicroRest 请求代理
 */
@Slf4j
@Component
@AllArgsConstructor
public class MicroRestProxy extends AbstractInvocationDispatcher<ProxyStub, Void> {
    PostRequestHandler postRequestHandler;

    GetRequestHandler getRequestHandler;

    MicroPostRequestHandler microPostRequestHandler;
    @Override
    protected Object invoke(StubProxyContext<ProxyStub> stubProxyContext, Object proxy, Method method, Object[] args) throws Throwable {
        // Class<?> clazz = proxy.getClass();
        Post post = method.getAnnotation(Post.class);
        if (post != null){
            return postRequestHandler.handler(proxy,method,args);
        }
        Get get = method.getAnnotation(Get.class);
        if (get != null){
            return getRequestHandler.handler(proxy,method,args);
        }
        MicroPost microPost = method.getAnnotation(MicroPost.class);
        if (microPost != null){
            return microPostRequestHandler.handler(proxy,method,args);
        }
        MicroGet microGet = method.getAnnotation(MicroGet.class);
        if (microGet != null){
            return "GET接口";
        }

        return "陈秋生";
    }

}
