package org.lboot.mrest.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Validator;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.lboot.mrest.annotation.Post;
import org.lboot.mrest.exception.MicroRestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class PostRequestHandler implements RequestHandler{
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 设置计时器
        TimeInterval timer = DateUtil.timer();
        // 获取注解值
        Post get = method.getAnnotation(Post.class);
        // 获取请求地址
        String url = get.value();
        url = proxyUrl(url,method,args);

        // 添加请求头
        Map<String,Object> headers = proxyHeader(get.headers(),method,args);
        // 如果请求头为空，指定 json utf8
        Object contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        if (Validator.isEmpty(contentType)){
            headers.put(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        Request.Builder requestBuilder = new Request.Builder();
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        // 获取请求体
        Map<String,Object> body = proxyBody(proxy,method,args);
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), JSONUtil.toJsonStr(body));
        // 如果是表单
        if (headers.get(HttpHeaders.CONTENT_TYPE).equals(MediaType.APPLICATION_FORM_URLENCODED_VALUE)){
            FormBody.Builder formBody = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : body.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue().toString());
            }
            requestBody = formBody.build();
        }
        // 如果是表单
        if (headers.get(HttpHeaders.CONTENT_TYPE).equals(MediaType.MULTIPART_FORM_DATA_VALUE)){

        }
        // 获取参数列表
        OkHttpClient client = new OkHttpClient();
        Request request = requestBuilder
                .url(url)
                .post(requestBody)
                .build();
        log.info("接口构建用时: {} ms", timer.intervalRestart());
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
