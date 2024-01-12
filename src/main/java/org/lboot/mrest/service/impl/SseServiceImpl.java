package org.lboot.mrest.service.impl;

import cn.hutool.core.lang.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.lboot.mrest.domain.StreamResponse;
import org.lboot.mrest.service.SseMessageConverter;
import org.lboot.mrest.service.SseService;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseServiceImpl implements SseService {
    /**
     * socketId SseEmitter对象映射集
     */
    private static Map<String, StreamResponse> sseEmitterMap = new ConcurrentHashMap<>();
    @Override
    @SneakyThrows
    public StreamResponse connect(String socketId) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        // 是否为空
        if(Validator.isEmpty(sseEmitter)){
            sseEmitter = new StreamResponse();
        }

        // 连接成功需要返回数据，否则会出现待处理状态
//        sseEmitter.send(SseEmitter.event().comment(""));

        // 连接断开
        sseEmitter.onCompletion(() -> {
            log.warn("{}:连接断开", socketId);
            sseEmitterMap.remove(socketId);
        });

        // 连接超时
        sseEmitter.onTimeout(() -> {
            log.warn("{}:连接超时", socketId);
            sseEmitterMap.remove(socketId);
        });

        // 连接报错
        sseEmitter.onError((throwable) -> {
            log.warn("{}:连接出错", socketId);
            sseEmitterMap.remove(socketId);
        });

        sseEmitterMap.put(socketId, sseEmitter);

        return sseEmitter;
    }


    @Override
    @SneakyThrows
    public void sendMessage(String socketId, Object message) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        sendMessage(sseEmitter, message);
    }

    @Override
    @SneakyThrows
    public void sendMessage(SseEmitter sseEmitter, Object message) {
        sseEmitter.send(message, MediaType.APPLICATION_JSON);
    }

    @Override
    public void complete(String socketId) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        sseEmitter.complete();
    }

    @Override
    @SneakyThrows
    @Async
    public void proxy(String socketId, Response response, String signal) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        String line;
        if (response.isSuccessful()){
            log.info("请求成功");
        }else {
            log.info(response.message());
            log.info(response.body().string());
        }
        while ((line = response.body().source().readUtf8Line()) != null) {
            if (line.contains(signal)) {
                log.warn("检测到关闭信号:{}", signal);
                response.close();
                complete(socketId);
                break;
            } else if (line.startsWith("data: ")) {
                line = line.substring(6);
                // 发送信息
                sendMessage(sseEmitter, line);
            }
        }
    }

    @Override
    @SneakyThrows
    @Async
    public void proxy(String socketId, Response response, String signal, SseMessageConverter converter) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        String line;
        if (response.isSuccessful()){
            log.info("请求成功");
        }else {
            log.info(response.message());
            log.info(response.body().string());
        }
        while ((line = response.body().source().readUtf8Line()) != null) {
            if (line.contains(signal)) {
                log.warn("检测到关闭信号:{}", signal);
                response.close();
                complete(socketId);
                break;
            } else if (line.startsWith("data: ")) {
                line = line.substring(6);
                // 发送信息
                sendMessage(sseEmitter, converter.convert(line));
            }
        }
    }
}
