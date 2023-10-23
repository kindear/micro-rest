package org.lboot.mrest.handler;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.annotation.Post;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@AllArgsConstructor
public class PostRequestHandler implements RequestHandler{
    @Override
    @SneakyThrows
    public Object handler(Object proxy, Method method, Object[] args) {
        // 获取注解值
        Post post = method.getAnnotation(Post.class);
        // 获取请求地址
        String url = post.url();


        // 获取参数列表
//        OkHttpClient client = new OkHttpClient();
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
//
//        Request request = new Request.Builder()
//                .url("http://www.example.com/api/endpoint")
//                .post(requestBody)
//                .build();
        return "post test!";

    }
}
