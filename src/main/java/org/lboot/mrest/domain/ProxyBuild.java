package org.lboot.mrest.domain;

import cn.hutool.core.lang.Validator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;
import java.util.ArrayList;
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
     * 请求地址
     */
    String url;
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
        List<String> headers = new ArrayList<>();
        for (String key:headerMap.keySet()){
            headers.add(key + " : " + headerMap.get(key));
        }
        setHeaders(headers);

    }

    public void buildBody(Map<String,Object> bodyMap){
        List<String> body = new ArrayList<>();
        for (String key:bodyMap.keySet()){
            body.add(key + " : " + bodyMap.get(key));
        }
        setBody(body);
    }


    /**
     * 打印接口构建 / 执行相关信息
     */
    @Async
    public void print(){
        StringBuilder headerSb = new StringBuilder();
        if (Validator.isNotEmpty(headers)){
            headers.forEach(ele->{
                headerSb.append(ele).append("\n");
            });
        }

        StringBuilder bodySb = new StringBuilder();
        if (Validator.isNotEmpty(body)){
            body.forEach(ele->{
                bodySb.append(ele).append("\n");
            });
        }

        log.info("\n|请求地址      |{}                                               \n" +
                "|请求方法      |{}                                              \n" +
                "|构建时长      |{}ms                                              \n" +
                "|执行时长      |{}ms                                              \n" +
                "----------------------------------------\n" +
                "请求头 headers:\n" +
                "{}      \n" +
                "----------------------------------------\n" +
                "请求参数 body:\n" +
                "{}\n" +
                "----------------------------------------\n",url,method,proxyRequestCost,executeRequestCost, headerSb, bodySb);
    }
}
