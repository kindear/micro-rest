package org.lboot.mrest.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.lboot.mrest.annotation.Decorator;
import org.lboot.mrest.annotation.Get;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ProxyContextDecorator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class GetRequestHandler implements RequestHandler{
    @Resource
    ApplicationContext context;
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置统计信息
        ProxyBuild proxyBuild = new ProxyBuild();
        proxyBuild.setMethod("GET");
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        Get get = method.getAnnotation(Get.class);
        // 获取是否存在装饰器
        Decorator decorator = method.getAnnotation(Decorator.class);
        // 装饰器不为空
        if (Validator.isNotEmpty(decorator)){
            ProxyContextDecorator proxyContextDecorator = decorator.value().newInstance();
            //log.info(proxyContextDecorator.readHeader().toString());
        }
        // 获取请求地址
        String url = get.value();
        url = proxyUrl(url,method,args);
        Map<String,Object> headers = proxyHeader(get.headers(),method,args);

        proxyBuild.buildHeaders(headers);
        // 添加请求头
        Request.Builder requestBuilder = new Request.Builder();
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        // 获取参数列表
        OkHttpClient client = new OkHttpClient();
        Request request = requestBuilder
                .url(url)
                .get()
                .build();
        // 记录接口构建时间
        proxyBuild.setProxyRequestCost(timer.intervalRestart());
        Response response = client.newCall(request).execute();
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
