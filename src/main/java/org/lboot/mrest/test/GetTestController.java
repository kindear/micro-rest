package org.lboot.mrest.test;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.lboot.mrest.test.service.AuthApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("testget")
@Api(tags = "GET 接口测试")
@AllArgsConstructor
public class GetTestController {
    AuthApi authApi;
    @GetMapping("userInfo")
    public Object getUserInfo(){
        String token = StpUtil.getTokenValue();
        return authApi.getUserInfo(token);
    }
}
