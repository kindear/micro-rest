package org.lboot.mrest.service;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author kindear
 * 代理上下文接口
 */
public abstract class ProxyContextDecorator {
    public Map<String,Object> readHeader(){
        HashMap<String,Object> header = new HashMap<>();
        HttpServletRequest request =((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            header.put(headerName,request.getHeader(headerName));
        }
        return header;
    }
    public Map<String,Object> readBody(){
        HashMap<String,Object> body = new HashMap<>();
        HttpServletRequest request =((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Enumeration<String> bodyNames = request.getParameterNames();
        while (bodyNames.hasMoreElements()){
            String paramKey = bodyNames.nextElement();
            body.put(paramKey,request.getParameter(paramKey));
        }
        return body;
    }
}
