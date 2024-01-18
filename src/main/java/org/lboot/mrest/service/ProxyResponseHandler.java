package org.lboot.mrest.service;

import cn.hutool.core.lang.Validator;
import lombok.SneakyThrows;
import okhttp3.Response;
import org.lboot.mrest.exception.MicroRestException;

/**
 * @author kindear
 * 请求处理器
 */
public abstract class ProxyResponseHandler{
    public String onSuccess(String body){
        return body;
    }

    @SneakyThrows
    public MicroRestException onFailure(Response response){
        String message = null;
        if (response.body() != null) {
            message = response.body().string();
        }
        if (Validator.isEmpty(message)){
            message = response.message();
        }
        Integer code = response.code();
        return new MicroRestException(code,message);
    }
}
