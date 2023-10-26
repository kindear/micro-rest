package org.lboot.mrest.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Request;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author kindear
 * 代理构建相关类
 */
@Slf4j
@Data
public class ProxyBuild implements Serializable {

    /**
     * 生成接口耗时间
     */
    long proxyRequestCost;

    /**
     * 接口执行耗费时间
     */
    long executeRequestCost;

    /**
     * 请求方法
     */
    String method;
    /**
     * 请求头
     */
    List<String> headers;

    /**
     * 请求体信息
     */
    List<String> body;

    public void buildHeaders(Map<String, Object> headerMap){
        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            headers.add(entry.getKey() + " : " + entry.getValue());
        }
    }

    public void buildBody(Map<String,Object> bodyMap){
        for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
            body.add(entry.getKey() + " : " + entry.getValue());
        }
    }

    public void readRequest(Request request){
        setMethod(request.method());
        // 读取请求头信息
        Headers reqHeaders = request.headers();
        int headersSize = reqHeaders.size();
        for (int i=0;i<headersSize;i++){
            headers.add(reqHeaders.name(i) + " : " + reqHeaders.value(i));
        }

    }

    /**
     * 打印接口构建 / 执行相关信息
     */
    @Async
    public void print(){

    }
}
