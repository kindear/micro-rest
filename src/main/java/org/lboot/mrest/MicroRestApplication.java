package org.lboot.mrest;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableKnife4j
@EnableAsync
@SpringBootApplication
public class MicroRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroRestApplication.class, args);
    }

}
