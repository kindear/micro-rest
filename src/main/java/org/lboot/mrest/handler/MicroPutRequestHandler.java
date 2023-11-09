package org.lboot.mrest.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.lboot.mrest.annotation.MicroPut;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ServiceResolution;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MicroPutRequestHandler implements RequestHandler{
    @Resource
    ApplicationContext context;

    ServiceResolution serviceResolution;
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置统计信息
        ProxyBuild proxyBuild = new ProxyBuild();
        proxyBuild.setMethod("PUT");
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        MicroPut microPut = method.getAnnotation(MicroPut.class);
        // 获取服务名称
        String serviceName = microPut.serviceName();
        // 获取分组名称
        String groupName = microPut.groupName();
        // 获取请求地址
        String url = null;
        if (Validator.isEmpty(groupName)){
            url = serviceResolution.resolve(serviceName) + microPut.path();
        }
        else {
            url = serviceResolution.resolve(groupName,serviceName) + microPut.path();
        }
        if (!url.startsWith("http")){
            url = "http://" + url;
        }
        url = proxyUrl(url,method,args);

        // 添加请求头
        Map<String,Object> headers = proxyHeader(microPut.headers(),method,args);
        // 如果请求头为空，指定 json utf8
        Object contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        proxyBuild.buildHeaders(headers);
        if (Validator.isEmpty(contentType)){
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        Request.Builder requestBuilder = new Request.Builder();
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        // 获取请求体
        Map<String,Object> body = proxyBody(proxy,method,args);
        proxyBuild.buildBody(body);
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), JSONUtil.toJsonStr(body));
        // 如果是表单
        if (headers.get(HttpHeaders.CONTENT_TYPE).equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)){
            FormBody.Builder formBody = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : body.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue().toString());
            }
            requestBody = formBody.build();
        }
        // 获取参数列表
        OkHttpClient client = new OkHttpClient();
        Request request = requestBuilder
                .url(url)
                .put(requestBody)
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
            // Proxy Request Error 需要抛出异常
            String message = response.message();
            Integer code = response.code();
            throw new MicroRestException(code,message);
        }

    }
}
