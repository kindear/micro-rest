package org.lboot.mrest.test.service;

import org.lboot.mrest.annotation.*;
import org.lboot.mrest.test.domain.AuthInfo;

@MicroRest
public interface AuthApi {
    @MicroGet(serviceName = "nepu-hrm",path = "/system/user/info")
    AuthInfo getUserInfo(@Headers("token") String token);

    @Get(url = "www.baidu.com")
    @Decorator
    public Object testBaidu();

}
