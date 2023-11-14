package org.lboot.mrest.service;

import lombok.SneakyThrows;

import java.util.Map;

/**
 * @author kindear
 * 代理上下文接口
 */
public abstract class ProxyContextDecorator {
    public Map<String,Object> proxyHeader(){
        return null;
    }
    public Map<String,Object> proxyBody(){
        return null;
    }
}
