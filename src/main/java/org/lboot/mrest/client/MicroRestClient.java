package org.lboot.mrest.client;

import cn.hutool.core.lang.Validator;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    /**
     * 客户端超时配置
     */
    private ClientTimeout clientTimeout = new ClientTimeout();

    // 连接超时配置
    public MicroRestClient connectTimeout(Integer connectionTimeout){
        clientTimeout.setConnectTimeout(connectionTimeout);
        return this;
    }

    // 读取超时配置
    public MicroRestClient readTimeout(Integer readTimeout){
        clientTimeout.setReadTimeout(readTimeout);
        return this;
    }

    // 写入超时配置
    public MicroRestClient writeTimeout(Integer writeTimeout){
        clientTimeout.setWriteTimeout(writeTimeout);
        return this;
    }


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
     * 推测 MediaType
     * @param file
     * @return
     */
    private static MediaType getMediaType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(file.getName());
        return MediaType.parse(contentType);
    }

    /**
     * 获取请求信息
     * @return
     */
    @SneakyThrows
    public Request getRequest(){
        if(!ok){
            log.error("接口构建错误，无法执行");
            return null;
        }


        // 如果设置了 url 则不使用 serviceName 查询
        Request.Builder requestBuilder = new Request.Builder();
        for (Map.Entry<String, Object> entry : header.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), JSONUtil.toJsonStr(body));
        // 如果是表单 --> 请求头类型不为空
        if (Validator.isNotEmpty(header.get(HttpHeaders.CONTENT_TYPE))){
            String contentType = header.get(HttpHeaders.CONTENT_TYPE).toString();
            log.info(contentType);
            if (contentType.startsWith("application/x-www-form-urlencoded")){
                FormBody.Builder formBody = new FormBody.Builder();
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    formBody.add(entry.getKey(), entry.getValue().toString());
                }
                requestBody = formBody.build();
            }else if (contentType.startsWith("multipart/form-data")){

                //log.info("表单文件上传");
                // 获取文件信息
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    if (entry.getValue() instanceof File){
                        File file = (File) entry.getValue();
                        builder.addFormDataPart(entry.getKey(), file.getName(),
                                RequestBody.create(MediaType.parse("application/octet-stream"), file));
                    }else {
                        // 构建非 File 参数
                        builder.addFormDataPart(entry.getKey(),entry.getValue().toString());
                    }
                }
                requestBody = builder.build();

            }
        }
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
            return request;
        }
        if (method.equals("POST")){
            Request request = requestBuilder
                    .url(url)
                    .post(requestBody)
                    .build();
            return request;
        }
        if (method.equals("DELETE")){
            Request request = requestBuilder
                    .url(url)
                    .delete()
                    .build();
            return request;
        }
        if (method.equals("PUT")){
            Request request = requestBuilder
                    .url(url)
                    .put(requestBody)
                    .build();
            return request;
        }

        if (method.equals("PATCH")){
            Request request = requestBuilder
                    .url(url)
                    .patch(requestBody)
                    .build();
            return request;
        }
        if (method.equals("HEAD") || method.equals("OPTIONS")){
            Request request = requestBuilder
                    .url(url)
                    .head()
                    .build();
            return request;
        }
        log.error("构建错误，无匹配构建模式");
        return null;
    }
    /**
     * 接口定义执行
     * @return
     */
    @SneakyThrows
    public Response execute(){
        // log.info(clientTimeout.toString());
        // 请求客户端
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(clientTimeout.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(clientTimeout.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(clientTimeout.getReadTimeout(), TimeUnit.SECONDS)
                .build();
        return client.newCall(getRequest()).execute();

    }
//    @SneakyThrows
//    @Async
//    public void buildSSE(SseEmitter sseEmitter, Response response, String signal){
//        String line;
//        if (response.isSuccessful()){
//            log.info("请求成功");
//        }else {
//            log.info(response.message());
//            log.info(response.body().string());
//        }
//        while ((line = response.body().source().readUtf8Line()) != null) {
//            if (line.contains(signal)) {
//                log.info("请求终止信号:{}", line);
//
//                log.info(response.body().string());
//                response.close();
//                log.info("请求关闭");
//                sseEmitter.complete();
//                break;
//            } else if (line.startsWith("data: ")) {
//                line = line.substring(6);
//                sseEmitter.send(line, org.springframework.http.MediaType.APPLICATION_JSON);
//                JSONObject responseJson = new JSONObject(line);
//                if (responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("delta").has("content")) {
//                    System.out.print(responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content"));
//                }
//            }
//        }
//    }

    public static void main(String[] args) throws IOException {
       // MicroRestClient client = new MicroRestClient().method(HttpMethod.GET).url("http://localhost:8080").addQuery("name","kindear");

//        Response response = new MicroRestClient().method(HttpMethod.GET).url("https://jsonplaceholder.typicode.com/posts/2").execute();
//        log.info(response.body().string());


    }
}
