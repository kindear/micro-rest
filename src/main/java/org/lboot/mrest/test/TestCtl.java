package org.lboot.mrest.test;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.ServiceResolution;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class TestCtl {
    TestApi testApi;

    ServiceResolution serviceResolution;


    @GetMapping("login")
    public Object doLogin(){
        /**
         * {
         *   "code": "",
         *   "password": "",
         *   "userName": "",
         *   "uuid": ""
         * }
         */
        Map<String,Object> params = new HashMap<>();
        params.put("code","bjf3");
        params.put("userName","admin");
        params.put("uuid","captcha_code:997aa2f7-6d48-4db8-aa42-16b4b27516ef");
        params.put("password","123456");
        return testApi.doLogin(params);
    }

    @GetMapping("get")
    public Object doGet(){
        return testApi.doGet();
    }


    @GetMapping("userInfo")
    public Object getUserInfo(){
        String token = "cb621fb6-4b91-453c-a2e0-f249b60c6993";
        return testApi.getUserInfo(token);
    }

}
