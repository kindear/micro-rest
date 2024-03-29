package org.lboot.mrest.service.impl;

import cn.hutool.core.lang.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.lboot.mrest.domain.StreamResponse;
import org.lboot.mrest.event.SseMessageCompleteEvent;
import org.lboot.mrest.service.SseMessageConverter;
import org.lboot.mrest.service.SseService;

import org.lboot.mrest.service.loader.SseHookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseServiceImpl implements SseService {
    /**
     * socketId SseEmitter对象映射集
     */
    private static Map<String, StreamResponse> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * socketId StringBuffer 完整消息映射
     * 只支持读取一次，读取一次后清空
     */
    private static Map<String, StringBuffer> sseMsgMap = new ConcurrentHashMap<>();

    // Hook 服务列表注入
    @Resource
    Optional<List<SseHookService>> hookServicesOp;

    /**
     * 应用程序上下文
     */
    @Resource
    ApplicationContext context;

    @Override
    @SneakyThrows
    public StreamResponse connect(String socketId) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        // 是否为空
        if(Validator.isEmpty(sseEmitter)){
            sseEmitter = new StreamResponse();
        }
        // 构建消息构建起
        StringBuffer sb = sseMsgMap.get(socketId);
        if (Validator.isEmpty(sb)){
            sb = new StringBuffer();
        }

        // 连接断开
        sseEmitter.onCompletion(() -> {
            log.warn("{}:连接断开", socketId);
            // 移除消息
            sseEmitterMap.remove(socketId);
            sseMsgMap.remove(socketId);

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

        sseMsgMap.put(socketId,sb);
        // 执行 Hook onConnect 服务
        if (hookServicesOp.isPresent()){
            List<SseHookService> hookServices = hookServicesOp.get();
            for (SseHookService service:hookServices){
                service.onConnect(socketId);
            }
        }

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
        // 获取字符串构建器
        StringBuffer sb = sseMsgMap.get(socketId);
        String msg = sb.toString();
        context.publishEvent(new SseMessageCompleteEvent(this,msg));
        // 执行 Hook onCompletion服务
        if (hookServicesOp.isPresent()){
            List<SseHookService> hookServices = hookServicesOp.get();
            for (SseHookService service:hookServices){
                service.onCompletion(socketId, msg);
            }
        }
        sseEmitter.complete();
    }

    @Override
    @SneakyThrows
    @Async
    public void proxy(String socketId, Response response, String signal) {
        StreamResponse sseEmitter = sseEmitterMap.get(socketId);
        // 构建字符串组合，存入内存缓存，只允许读取一次 -->
        StringBuffer sb = sseMsgMap.get(socketId);
        String line;
        if (!response.isSuccessful()){
            log.info(response.message());
            log.info(response.body().string());
        }
        while ((line = response.body().source().readUtf8Line()) != null) {
            if (line.contains(signal)) {
                log.warn("检测到关闭信号:{}", signal);
                response.close();
                complete(socketId);
                break;
            }  else if (line.startsWith("data: ")) {
                line = line.substring(6);
                // 附加信息
                sb.append(line);
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
        // 构建字符串组合，存入内存缓存，只允许读取一次 -->
        StringBuffer sb = sseMsgMap.get(socketId);
        String line;
        if (!response.isSuccessful()){
            log.info(response.message());
            log.info(response.body().string());
        }
        while ((line = response.body().source().readUtf8Line()) != null) {
            if (line.contains(signal)) {
                log.warn("检测到关闭信号:{}", signal);
                response.close();
                complete(socketId);
                break;
            } else if (line.startsWith("data: ")){
                line = line.substring(6);

                String sendMsg = converter.convert(line);
                // 附加信息
                sb.append(sendMsg);
                // 发送信息
                sendMessage(sseEmitter, converter.convert(sendMsg));
            }
        }
    }
}
