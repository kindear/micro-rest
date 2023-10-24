package org.lboot.mrest.handler;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.lboot.mrest.annotation.Get;
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

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取注解值
        Get get = method.getAnnotation(Get.class);
        // 获取请求地址
        String url = get.value();
        url = proxyUrl(url,method,args);
        Map<String,Object> headers = proxyHeader(get.headers(),method,args);
        stopWatch.stop();
        log.info("\n代理构建请求用时:{} ms\n", stopWatch.getTotalTimeMillis());
        StringBuilder logStr = new StringBuilder();
        logStr.append("=========请求信息============\n");
        logStr.append("请求地址:").append(url).append("\n");
        logStr.append("请求头:\n");

        for (String key: headers.keySet()){
            logStr.append(key).append(" : ").append(headers.get(key)).append("\n");
        }
        log.info("\n{}",logStr);


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
                log.debug("请求返回值为空");
            }

//            return JSONUtil.toBean(responseBody,method.getReturnType());
        } else {
            System.out.println("Request failed: " + response.code());
        }
        return "get test!";
    }
}
