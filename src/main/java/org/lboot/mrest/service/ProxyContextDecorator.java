package org.lboot.mrest.service;

import java.util.Map;

/**
 * @author kindear
 * 代理上下文接口
 */
public abstract class ProxyContextDecorator {
    public Map<String,Object> readHeader(){
        return null;
    }
    public Map<String,Object> readBody(){
        return null;
    }
}
