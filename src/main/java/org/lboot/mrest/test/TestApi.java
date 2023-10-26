package org.lboot.mrest.test;

import org.lboot.mrest.annotation.Get;
import org.lboot.mrest.annotation.MicroRest;
import org.lboot.mrest.annotation.Post;
import org.lboot.mrest.annotation.Query;
import org.lboot.mrest.test.dto.PageQueryDTO;
import org.lboot.mrest.test.dto.PageResultDTO;

@MicroRest
public interface TestApi {

    @Post("http://localhost:8080")
    public String doPost(String name);

    @Get(value = "http://wellb.apisev.cn/ufs/file")
    public PageResultDTO<Object> doGet(@Query PageQueryDTO dto);
}
