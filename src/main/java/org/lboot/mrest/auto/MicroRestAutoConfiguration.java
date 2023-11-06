package org.lboot.mrest.auto;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

/**
 * @author kindear
 * RABC 模块
 */
@Slf4j
@AutoConfiguration
@ComponentScan(basePackages = {
        "org.lboot.mrest"
})
public class MicroRestAutoConfiguration implements EnvironmentAware {
    @Override
    public void setEnvironment(Environment environment) {
        // 此处注册全局环境
    }
}
