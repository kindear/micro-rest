package org.lboot.mrest.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.lboot.mrest.annotation.Decorator;
import org.lboot.mrest.annotation.Delete;
import org.lboot.mrest.annotation.ResponseHandler;
import org.lboot.mrest.client.MicroRestClient;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ProxyContextDecorator;
import org.lboot.mrest.service.ProxyResponseHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class DeleteRequestHandler implements RequestHandler{
    @Resource
    ApplicationContext context;
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置统计信息
        ProxyBuild proxyBuild = new ProxyBuild();
        proxyBuild.setMethod("DELETE");
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        Delete delete = method.getAnnotation(Delete.class);
        // 获取请求地址
        String url = delete.value();
        if (Validator.isEmpty(url)){
            url = delete.url();
        }
        url = proxyUrl(url,method,args);
        // 构建请丢地址
        proxyBuild.setUrl(url);
        // 构建请求头
        Map<String,Object> headers = proxyHeader(delete.headers(),method,args);
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
        }
        // 构建记录加入
        proxyBuild.buildHeaders(headers);
        MicroRestClient client = new MicroRestClient()
                .url(url)
                .connectTimeout(delete.connectTimeout())
                .readTimeout(delete.readTimeout())
                .writeTimeout(delete.writeTimeout())
                .method(HttpMethod.DELETE)
                .header(headers);
        // 记录接口构建时间
        proxyBuild.setProxyRequestCost(timer.intervalRestart());
        Response response = client.execute();
        // 记录接口执行时间
        proxyBuild.setExecuteRequestCost(timer.intervalRestart());
        // 发布事件
        context.publishEvent(new ProxyRequestExecuteEvent(this, proxyBuild));
        // 判断是否存在相应处理器接口
        ResponseHandler responseHandler = method.getAnnotation(ResponseHandler.class);
        if (response.isSuccessful()) {
            if (response.body() != null) {
                String bodyStr = response.body().string();
                if (Validator.isNotEmpty(responseHandler)){
                    ProxyResponseHandler proxyResponseHandler = responseHandler.value().newInstance();
                    bodyStr = proxyResponseHandler.onSuccess(bodyStr);
                }
                return JSONUtil.toBean(bodyStr,method.getReturnType(),true);
            }else {
                return null;
            }
        } else {
            if (Validator.isNotEmpty(responseHandler)){
                ProxyResponseHandler proxyResponseHandler = responseHandler.value().newInstance();
                throw proxyResponseHandler.onFailure(response);
            }
            // Proxy Request Error 需要抛出异常
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
