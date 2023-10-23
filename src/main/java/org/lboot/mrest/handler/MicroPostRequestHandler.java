package org.lboot.mrest.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class MicroPostRequestHandler implements RequestHandler{
    @Override
    public Object handler(Object proxy, Method method, Object[] args) {
        return "POST请求";
    }
}
