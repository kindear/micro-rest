package org.lboot.mrest.test;

import org.lboot.mrest.annotation.*;

import java.util.List;
import java.util.Map;

@MicroRest
public interface TestApi {

    @MicroPost(serviceName = "nepu-hrm",path = "/system/login")
    public String doLogin(@Body Map<String,Object> params);

    @MicroGet(serviceName = "nepu-hrm",path = "/hrm/job/grade/types")
    public List<Object> doGet();

    @Get("http://localhost:8080/rbac/status")
    public Object getLoginStatus(@Headers("token") String token);

    @MicroGet(serviceName = "nepu-hrm",path = "/system/user/info")
    Object getUserInfo(@Headers("token") String token);
}
