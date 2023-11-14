package org.lboot.mrest.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.ProxyContextDecorator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DefaultProxyContextDecorator extends ProxyContextDecorator {
    @Override
    @SneakyThrows
    public Map<String, Object> proxyHeader() {
        HttpServletRequest request =((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return null;
    }


}
