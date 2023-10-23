package org.lboot.mrest.test;

import org.lboot.mrest.annotation.*;
import org.lboot.mrest.test.dto.PageQueryDTO;

@MicroRest
public interface TestApi {

    @Post("http://localhost:8080")
    public String doPost(String name);

    @Get("http://localhost:8080/{id}")
    public String doGet(@PathVar("id") String id, @Query PageQueryDTO PageQueryDTO);
}
