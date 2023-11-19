package org.lboot.mrest.client;

import cn.hutool.core.lang.Validator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author kindear
 * 请求客户端
 */
@Slf4j
@Data
public class MicroRestClient {

    String serviceName;

    String url;

    String method;

    Map<String, Object> header;

    Map<String,Object> body;

    public MicroRestClient serviceName(String serviceName){
        if (Validator.isEmpty(serviceName)){
            log.error("服务名称参数不可为空");
        }else {
            this.serviceName = serviceName;
        }
        return this;
    }


    public MicroRestClient method(HttpMethod httpMethod){
        return method(httpMethod.name());
    }

    public MicroRestClient method(String method){
        List<String> methods = Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS","TRACE");

        if (methods.contains(method)){
            this.method = method;
        }else {
            log.error("{} 请求方式不支持",method);
        }
        return this;
    }

    public MicroRestClient url(String url){

        if (Validator.isUrl(url)){
            this.url = url;
            return this;
        }
        else {
            log.error("{} 不符合请求地址定义形式", url);
            return this;
        }

    }


    /**
     * 接口定义执行
     * @return
     */
    public Response execute(){
        // 如果设置了 url 则不使用 serviceName 查询
        if (Validator.isNotEmpty(url)){

        }
        return null;
    }

    public static void main(String[] args) {
        MicroRestClient client = new MicroRestClient().method(HttpMethod.GET).url("http://localhost:8080");
        log.info(client.toString());
    }
}
