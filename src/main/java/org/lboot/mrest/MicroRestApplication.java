package org.lboot.mrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MicroRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroRestApplication.class, args);
    }

}
