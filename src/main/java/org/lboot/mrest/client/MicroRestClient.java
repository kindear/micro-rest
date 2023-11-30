package org.lboot.mrest.client;

import cn.hutool.core.lang.Validator;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kindear
 * 请求客户端
 */
@Slf4j
@Data
public class MicroRestClient {


    String url;

    // 设置默认GET请求
    String method = "GET";

    Map<String,Object> query = new HashMap<>();

    Map<String, Object> header = new HashMap<>();

    Map<String,Object> body = new HashMap<>();

    private boolean ok = true;

    public MicroRestClient header(Map<String,Object> header){
        // 如果不为空
        if (!this.header.isEmpty()){
            this.header.putAll(header);
            return this;
        }
        this.header = header;
        return this;
    }

    public MicroRestClient addHeader(String key, Object val){
        this.header.put(key,val);
        return this;
    }

    public MicroRestClient body(Map<String,Object> body){
        if (!this.body.isEmpty()){
            this.body.putAll(body);
            return this;
        }
        this.body = body;
        return this;
    }

    public MicroRestClient addBody(String key, Object val){
        this.body.put(key,val);
        return this;
    }

    public MicroRestClient query(Map<String,Object> query){
        if (!this.query.isEmpty()){
            this.query.putAll(query);
            return this;
        }
        this.query = query;
        return this;
    }

    public MicroRestClient addQuery(String key, Object val){
        if (Validator.isEmpty(this.query)){
            HashMap<String,Object> t = new HashMap<>();
            t.put(key,val);
            this.query = t;
            return this;
        }
        this.query.put(key,val);
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
            ok = false;
        }
        return this;
    }

    public MicroRestClient url(String url){

        if (Validator.isUrl(url)){
            this.url = url;

        }
        else {
            log.error("{} 不符合请求地址定义形式", url);
            ok = false;
        }
        return this;

    }


    /**
     * 接口定义执行
     * @return
     */
    @SneakyThrows
    public Response execute(){
        //log.info(toString());
        if(!ok){
            log.error("接口构建错误，无法执行");
            return null;
        }
        // 请求客户端
        OkHttpClient client = new OkHttpClient();
        // 如果设置了 url 则不使用 serviceName 查询
        Request.Builder requestBuilder = new Request.Builder();
        for (Map.Entry<String, Object> entry : header.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        // 构建请求体
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), JSONUtil.toJsonStr(body));
        // 构建请求地址
        if (Validator.isNotEmpty(query)){
            String queryUrl = HttpUtil.toParams(query);
            if (this.url.contains("?")){
                this.url = url + "&" + queryUrl;
            }else {
                this.url =  url + "?" + queryUrl;
            }

        }
        // 执行请求
        if (method.equals("GET")){
            Request request = requestBuilder
                    .url(url)
                    .get()
                    .build();
            return client.newCall(request).execute();
        }
        if (method.equals("POST")){
            Request request = requestBuilder
                    .url(url)
                    .post(requestBody)
                    .build();
            return client.newCall(request).execute();
        }
        if (method.equals("DELETE")){
            Request request = requestBuilder
                    .url(url)
                    .delete()
                    .build();
            return client.newCall(request).execute();
        }
        if (method.equals("PUT")){
            Request request = requestBuilder
                    .url(url)
                    .put(requestBody)
                    .build();
            return client.newCall(request).execute();
        }

        if (method.equals("PATCH")){
            Request request = requestBuilder
                    .url(url)
                    .patch(requestBody)
                    .build();
            return client.newCall(request).execute();
        }
        if (method.equals("HEAD") || method.equals("OPTIONS")){
            Request request = requestBuilder
                    .url(url)
                    .head()
                    .build();
            return client.newCall(request).execute();
        }
        log.error("请求条件匹配出错");
        return null;
    }

    public static void main(String[] args) throws IOException {
       // MicroRestClient client = new MicroRestClient().method(HttpMethod.GET).url("http://localhost:8080").addQuery("name","kindear");

        Response response = new MicroRestClient().method(HttpMethod.GET).url("https://jsonplaceholder.typicode.com/posts/2").execute();
        log.info(response.body().string());

        //log.info(client.toString());
    }
}
