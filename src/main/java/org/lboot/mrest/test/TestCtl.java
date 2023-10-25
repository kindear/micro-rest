package org.lboot.mrest.test;

import lombok.AllArgsConstructor;
import org.lboot.mrest.test.dto.PageQueryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TestCtl {
    TestApi testApi;

    @GetMapping("post")
    public Object doPost(){
        return testApi.doPost("kindear");
    }

    @GetMapping("get")
    public Object doGet(PageQueryDTO dto){
        dto.setPageSize(10);
        dto.setPageNum(1);
        return testApi.doGet(dto);
    }
}
