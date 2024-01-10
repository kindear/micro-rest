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
import org.lboot.mrest.annotation.MicroPost;
import org.lboot.mrest.client.MicroRestClient;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ProxyContextDecorator;
import org.lboot.mrest.service.ServiceResolution;
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
public class MicroPostRequestHandler implements RequestHandler{

    @Resource
    ApplicationContext context;

    ServiceResolution serviceResolution;
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置统计信息
        ProxyBuild proxyBuild = new ProxyBuild();
        proxyBuild.setMethod("POST");
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        MicroPost microPost = method.getAnnotation(MicroPost.class);
        // 获取服务名称
        String serviceName = microPost.serviceName();
        // 获取分组名称
        String groupName = microPost.groupName();
        // 获取请求地址
        String url = null;
        if (Validator.isEmpty(groupName)){
            url = serviceResolution.resolve(serviceName) + microPost.path();
        }
        else {
            url = serviceResolution.resolve(groupName,serviceName) + microPost.path();
        }

        if (!url.startsWith("http")){
            url = "http://" + url;
        }
        url = proxyUrl(url,method,args);
        // 存储请求地址
        proxyBuild.setUrl(url);
        // 添加请求头
        Map<String,Object> headers = proxyHeader(microPost.headers(),method,args);
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
        log.info(headers.toString());
        MicroRestClient client = new MicroRestClient()
                .url(url)
                .method(HttpMethod.POST)
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
            String message = null;
            if (response.body() != null) {
                message = response.body().string();
            }
            if (Validator.isEmpty(message)){
                message = response.message();
            }
            Integer code = response.code();
            throw new MicroRestException(code,message);
        }

    }
}
