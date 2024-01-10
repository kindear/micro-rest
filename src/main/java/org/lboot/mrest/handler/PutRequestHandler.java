package org.lboot.mrest.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.lboot.mrest.annotation.Decorator;
import org.lboot.mrest.annotation.Put;
import org.lboot.mrest.client.MicroRestClient;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ProxyContextDecorator;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class PutRequestHandler implements RequestHandler{
    @Resource
    ApplicationContext context;

    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置统计信息
        ProxyBuild proxyBuild = new ProxyBuild();
        proxyBuild.setMethod("PUT");
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        Put put = method.getAnnotation(Put.class);
        // 获取请求地址
        String url = put.value();
        if (Validator.isEmpty(url)){
            url = put.url();
        }
        url = proxyUrl(url,method,args);
        proxyBuild.setUrl(url);
        // 添加请求头
        Map<String,Object> headers = proxyHeader(put.headers(),method,args);
        // 获取请求体
        Map<String,Object> body = proxyBody(proxy,method,args);
        // 获取是否存在透传装饰器 --> 如果不存在则加入，透传优先级最低
        Decorator decorator = method.getAnnotation(Decorator.class);
        if (Validator.isNotEmpty(decorator)){
            ProxyContextDecorator proxyContextDecorator = decorator.value().newInstance();
            Map<String,Object> decoratorHeader = proxyContextDecorator.readHeader();
            for (String key:decoratorHeader.keySet()){
                if (!headers.containsKey(key)){
                    headers.put(key, decoratorHeader.get(key));
                }
            }
            Map<String,Object> decoratorBody = proxyContextDecorator.readBody();
            for (String key:decoratorBody.keySet()){
                if (!body.containsKey(key)){
                    body.put(key,decoratorBody.get(key));
                }
            }
        }
        // 构建记录加入
        proxyBuild.buildHeaders(headers);
        proxyBuild.buildBody(body);
        MicroRestClient client = new MicroRestClient()
                .url(url)
                .method(HttpMethod.PUT)
                .header(headers)
                .body(body);
        // 记录接口构建时间
        proxyBuild.setProxyRequestCost(timer.intervalRestart());
        Response response = client.execute();
        // 记录接口执行时间
        proxyBuild.setExecuteRequestCost(timer.intervalRestart());
        // 发布事件
        context.publishEvent(new ProxyRequestExecuteEvent(this, proxyBuild));
        if (response.isSuccessful()) {
            if (response.body() != null) {
                return JSONUtil.toBean(response.body().string(),method.getReturnType(),true);
            }else {
                return null;
            }
        } else {
            // Proxy Request Error 需要抛出异常
            String message = response.message();
            Integer code = response.code();
            throw new MicroRestException(code,message);
        }

    }
}
