package org.lboot.mrest.nacos;

import cn.hutool.json.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.nacos.domain.ServiceHost;
import org.lboot.mrest.service.ServiceResolution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

@Slf4j
public class NacosServiceResolution implements ServiceResolution {

    @Value("${nacos.discovery.server-addr}")
    private String discoveryHost;
    /**
     * 构建示例列表请求
     * @return
     */
    String buildInstanceListUrl(){
        return "http://" + discoveryHost + "/nacos/v2/ns/instance/list";
    }

    @Override
    @SneakyThrows
    public String resolve(String serviceName) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(buildInstanceListUrl()))
                .newBuilder()
                .addQueryParameter("serviceName", serviceName)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            if (responseBody != null) {
                String resultStr = responseBody.string();
                JSONObject jsonObject = new JSONObject(resultStr);
                if (!jsonObject.get("code",Integer.class).equals(0)){
                    throw new MicroRestException(HttpStatus.BAD_REQUEST.value(), jsonObject.get("message",String.class));
                }else {
                    String dataStr = jsonObject.getStr("data");
                    JSONObject dataObject = new JSONObject(dataStr);
                    List<ServiceHost> hosts = dataObject.getBeanList("hosts",ServiceHost.class);
                    if (!hosts.isEmpty()){
                        ServiceHost host = hosts.get(0);
                        if (host.getPort() == 80){
                            return host.getIp();
                        }else {
                            return host.getIp() + ":" + host.getPort();
                        }
                    }
                }
                log.info(resultStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String resolve(String groupName, String serviceName) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(buildInstanceListUrl()))
                .newBuilder()
                .addQueryParameter("serviceName", serviceName)
                .addQueryParameter("groupName",groupName)
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            if (responseBody != null) {
                String resultStr = responseBody.string();
                JSONObject jsonObject = new JSONObject(resultStr);
                if (!jsonObject.get("code",Integer.class).equals(0)){
                    throw new MicroRestException(HttpStatus.BAD_REQUEST.value(), jsonObject.get("message",String.class));
                }else {
                    String dataStr = jsonObject.getStr("data");
                    JSONObject dataObject = new JSONObject(dataStr);
                    List<ServiceHost> hosts = dataObject.getBeanList("hosts",ServiceHost.class);
                    if (!hosts.isEmpty()){
                        ServiceHost host = hosts.get(0);
                        if (host.getPort() == 80){
                            return host.getIp();
                        }else {
                            return host.getIp() + ":" + host.getPort();
                        }
                    }
                }
                log.info(resultStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
