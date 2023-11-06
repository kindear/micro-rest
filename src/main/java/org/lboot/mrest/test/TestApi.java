package org.lboot.mrest.test;

import org.lboot.mrest.annotation.MicroGet;
import org.lboot.mrest.annotation.MicroRest;
import org.lboot.mrest.annotation.Post;

import java.util.List;

@MicroRest
public interface TestApi {

    @Post("http://localhost:8080")
    public String doPost(String name);

    @MicroGet(serviceName = "nepu-hrm",path = "/hrm/job/grade/types")
    public List<Object> doGet();
}
