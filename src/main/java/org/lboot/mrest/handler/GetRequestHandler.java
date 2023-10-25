package org.lboot.mrest.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.lboot.mrest.annotation.Get;
import org.lboot.mrest.exception.MicroRestException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class GetRequestHandler implements RequestHandler{
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {

        // 获取注解值
        Get get = method.getAnnotation(Get.class);
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取请求地址
        String url = get.value();
        url = proxyUrl(url,method,args);
        Map<String,Object> headers = proxyHeader(get.headers(),method,args);
        log.info("接口代理构建用时: {} ms", timer.intervalRestart());


        // 获取参数列表
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
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
