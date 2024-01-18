package org.lboot.mrest.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.lboot.mrest.exception.MicroRestException;
import org.lboot.mrest.service.ProxyResponseHandler;
@Slf4j
public class DefaultProxyResponseHandler extends ProxyResponseHandler {
    @Override
    public String onSuccess(String body) {
        return super.onSuccess(body);
    }
    @Override
    public MicroRestException onFailure(Response response) {
        return super.onFailure(response);
    }
}
