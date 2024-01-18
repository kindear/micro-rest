package org.lboot.mrest.test.handler;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ProxyResponseHandler;

@Slf4j
public class ChatResponseHandler extends ProxyResponseHandler {
    @Override
    public String onSuccess(String body) {
        return super.onSuccess(body);
    }

    @Override
    @SneakyThrows
    public MicroRestException onFailure(Response response) {
        log.info("Chat专属响应失败处理");
        String body = response.body().string();
//        JSONObject jsonObject = JSONUtil.parseObj(body);
//        String errMsg = jsonObject.get("error").toString();
        return new MicroRestException(500, body);
    }
}
