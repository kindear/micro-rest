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
import org.lboot.mrest.annotation.MicroGet;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ServiceResolution;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MicroGetRequestHandler implements RequestHandler{
    @Resource
    ApplicationContext context;

    ServiceResolution serviceResolution;
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置统计信息
        ProxyBuild proxyBuild = new ProxyBuild();
        // 设置请求类型
        proxyBuild.setMethod("GET");
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        MicroGet microGet = method.getAnnotation(MicroGet.class);
        // 获取服务名称
        String serviceName = microGet.serviceName();
        // 获取分组名称
        String groupName = microGet.groupName();
        // 获取请求地址
        String url = null;
        if (Validator.isEmpty(groupName)){
            url = serviceResolution.resolve(serviceName) + microGet.path();
        }
        else {
            url = serviceResolution.resolve(groupName,serviceName) + microGet.path();
        }
        if (!url.startsWith("http")){
            url = "http://" + url;
        }
        url = proxyUrl(url,method,args);
        // 设置请求地址
        proxyBuild.setUrl(url);
        Map<String,Object> headers = proxyHeader(microGet.headers(),method,args);
        log.info(headers.toString());
        // 构建请求头
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
        proxyBuild.print();
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
