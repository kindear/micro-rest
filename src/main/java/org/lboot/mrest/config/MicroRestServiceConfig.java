package org.lboot.mrest.config;

import org.lboot.mrest.nacos.NacosServiceResolution;
import org.lboot.mrest.service.ServiceResolution;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author kindear
 * 服务实现配置
 */
@SpringBootConfiguration
public class MicroRestServiceConfig {

    @Bean
    @ConditionalOnMissingBean(value = ServiceResolution.class)
    public ServiceResolution serviceResolution(){
        return new NacosServiceResolution();
    }

}
