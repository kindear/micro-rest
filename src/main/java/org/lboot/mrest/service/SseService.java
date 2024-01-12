package org.lboot.mrest.service;

import okhttp3.Response;
import org.lboot.mrest.domain.StreamResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * <a href="https://blog.csdn.net/mouday/article/details/132612632">参考文档地址</a>
 */
public interface SseService {
    StreamResponse connect(String socketId);


    void sendMessage(String socketId, Object message);

    void sendMessage(SseEmitter sseEmitter, Object message);

    void complete(String socketId);

    /**
     * 请求代理, 实际上是通过okhttp实现SSE请求的转发
     * @param socketId
     * @param response
     * @param signal
     */
    void proxy(String socketId, Response response, String signal);

    /**
     * 请求代理, 实际上是通过okhttp实现SSE请求的转发
     * @param socketId
     * @param response
     * @param signal
     * @param converter 转化器
     */
    void proxy(String socketId, Response response, String signal, SseMessageConverter converter);

}