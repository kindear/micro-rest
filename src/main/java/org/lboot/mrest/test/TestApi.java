package org.lboot.mrest.test;

import org.lboot.mrest.annotation.Body;
import org.lboot.mrest.annotation.MicroGet;
import org.lboot.mrest.annotation.MicroPost;
import org.lboot.mrest.annotation.MicroRest;

import java.util.List;
import java.util.Map;

@MicroRest
public interface TestApi {

    @MicroPost(serviceName = "nepu-hrm",path = "/system/login")
    public String doLogin(@Body Map<String,Object> params);

    @MicroGet(serviceName = "nepu-hrm",path = "/hrm/job/grade/types")
    public List<Object> doGet();
}
