package org.lboot.mrest.nacos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MicroNacosDiscoveryConfig {

    @Value("${nacos.discovery.server-addr}")
    private String discoveryHost;

}
