package org.lboot.mrest.test;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.ServiceResolution;
import org.lboot.mrest.test.dto.PageQueryDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class TestCtl {
    TestApi testApi;

    ServiceResolution serviceResolution;


    @GetMapping("post")
    public Object doPost(){
        return testApi.doPost("kindear");
    }

    @GetMapping("get")
    public Object doGet(PageQueryDTO dto){
        String reqURl = serviceResolution.resolve("nepu-hrm");
        log.info(reqURl);
        dto.setPageSize(10);
        dto.setPageNum(1);
        return testApi.doGet(dto);
    }

}
