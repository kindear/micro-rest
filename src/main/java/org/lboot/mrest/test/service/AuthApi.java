package org.lboot.mrest.test.service;

import org.lboot.mrest.annotation.Headers;
import org.lboot.mrest.annotation.MicroGet;
import org.lboot.mrest.annotation.MicroRest;
import org.lboot.mrest.test.domain.AuthInfo;

@MicroRest
public interface AuthApi {
    @MicroGet(serviceName = "nepu-hrm",path = "/system/user/info")
    AuthInfo getUserInfo(@Headers("token") String token);

}
