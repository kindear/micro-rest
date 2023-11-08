package org.lboot.mrest.proxy;

import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import com.github.yungyu16.spring.stub.proxy.AbstractInvocationDispatcher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.annotation.*;
import org.lboot.mrest.handler.*;
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

    DeleteRequestHandler deleteRequestHandler;

    PutRequestHandler putRequestHandler;

    GetRequestHandler getRequestHandler;

    MicroPostRequestHandler microPostRequestHandler;

    MicroDeleteRequestHandler microDeleteRequestHandler;

    MicroPutRequestHandler microPutRequestHandler;

    MicroGetRequestHandler microGetRequestHandler;

    @Override
    protected Object invoke(StubProxyContext<ProxyStub> stubProxyContext, Object proxy, Method method, Object[] args) throws Throwable {
        // Class<?> clazz = proxy.getClass();
        // GET 查询最经常使用，放在前面 减少判断计算
        Get get = method.getAnnotation(Get.class);
        if (get != null){
            return getRequestHandler.handler(proxy,method,args);
        }
        MicroGet microGet = method.getAnnotation(MicroGet.class);
        if (microGet != null){
            return microGetRequestHandler.handler(proxy,method,args);
        }
        Post post = method.getAnnotation(Post.class);
        if (post != null){
            return postRequestHandler.handler(proxy,method,args);
        }
        MicroPost microPost = method.getAnnotation(MicroPost.class);
        if (microPost != null){
            return microPostRequestHandler.handler(proxy,method,args);
        }
        Put put = method.getAnnotation(Put.class);
        if (put != null){
            return putRequestHandler.handler(proxy,method,args);
        }
        MicroPut microPut = method.getAnnotation(MicroPut.class);
        if (microPut != null){
            return microPutRequestHandler.handler(proxy,method,args);
        }
        Delete delete = method.getAnnotation(Delete.class);
        if (delete != null){
            return deleteRequestHandler.handler(proxy,method,args);
        }
        MicroDelete microDelete = method.getAnnotation(MicroDelete.class);
        if (microDelete != null){
            return microDeleteRequestHandler.handler(proxy,method,args);
        }
        return getRequestHandler.handler(proxy,method,args);
    }

}
