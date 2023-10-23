package org.lboot.mrest.handler;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.annotation.Get;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@AllArgsConstructor
public class GetRequestHandler implements RequestHandler{
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {

        // 获取注解值
        Get get = method.getAnnotation(Get.class);
        // 获取请求地址
        String url = get.value();
        log.info("处理前请求地址:{}", url);
        url = proxyUrl(url,method,args);
        log.info("请求地址为:{}", url);
        // 获取参数列表
//        OkHttpClient client = new OkHttpClient();
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
//
//        Request request = new Request.Builder()
//                .url("http://www.example.com/api/endpoint")
//                .post(requestBody)
//                .build();
        return "get test!";

    }
}
