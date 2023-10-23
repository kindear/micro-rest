package org.lboot.mrest.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.http.HttpUtil;
import org.lboot.mrest.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kinder
 * 请求处理接口
 */
public interface RequestHandler {
    /**
     * 接口处理
     * @param proxy 代理对象
     * @param method 代理方法
     * @param args 方法参数列表
     * @return 执行结果
     */
    public Object handler(Object proxy, Method method, Object[] args);

    /**
     * 请求参数构建方法
     * @param url 请求地址
     * @param method 方法
     * @param args 参数列表
     * @return 请求地址
     */

    default String proxyUrl(String url, Method method, Object[] args){
        Parameter[] parameters = method.getParameters();
        Map<String,Object> pathMap = new HashMap<>();
        Map<String,Object> queryMap = new HashMap<>();
        // 构建请求地址
        for (int i = 0; i < parameters.length; i++){
            Parameter parameter = parameters[i];
            // 路径参数注解
            PathVar pathVar = parameter.getAnnotation(PathVar.class);
            if (Validator.isNotEmpty(pathVar)){
                if (!pathVar.value().isEmpty()){
                    pathMap.put(pathVar.value(), args[i]);
                }
            }
            // 请求参数注解
            Query query = parameter.getAnnotation(Query.class);
            if (Validator.isNotEmpty(query)){
                if (query.value().isEmpty() && isCustomClass(args[i].getClass())){
                    Map<String,Object> beanMap = BeanUtil.beanToMap(args[i]);
                    queryMap.putAll(beanMap);
                }else if (!query.value().isEmpty() && !isCustomClass(args[i].getClass())){
                    queryMap.put(query.value(), args[i]);
                }
            }
        }

        // 替换地址
        for (String pathKey:pathMap.keySet()){
            url = url.replace("{" + pathKey + "}" , pathMap.get(pathKey).toString());
        }
        String queryUrl = HttpUtil.toParams(queryMap);
        if (queryMap.isEmpty()){
            return url;
        }else {
            return url + "?" + queryUrl;
        }
    }
    public static boolean isCustomClass(Class<?> clazz) {
        // 检查类是否不是Java标准库或第三方库的类
        return !clazz.getName().startsWith("java.") && !clazz.getName().startsWith("javax.");
    }

    /**
     * 请求头信息默认组装方法
     * @param headers 请求头[]
     * @param method 方法名词
     * @param args 参数名词
     * @return 请求头 map
     */
    default Map<String,Object> proxyHeader(String []headers, Method method, Object[] args){
        // 提取
        Map<String,Object> headerMap = new HashMap<>();
        for (String header:headers){
            String[] headerParts = header.split(":");
            String headerKey = headerParts[0].trim();
            String headerVal = headerParts[1].trim();
            headerMap.put(headerKey,headerVal);
        }
        // 遍历字段提取 @Headers 信息
        for(Object object:args){
            Class<?> clazz = object.getClass();
            Headers headerAnno = clazz.getAnnotation(Headers.class);
            if (Validator.isNotEmpty(headerAnno)){
                if (headerAnno.value().isEmpty() && object instanceof Map){
                    // 提取 Map 相关信息
                    Map<String, Object> argMap = BeanUtil.beanToMap(object);
                    for (String key: argMap.keySet()){
                        // @Headers(val) > @Headers 优先级
                        if (Validator.isEmpty(headerMap.get(key))){
                            headerMap.put(key, argMap.get(key));
                        }
                    }
                }else {
                    String headerKey = headerAnno.value();
                    headerMap.put(headerKey, object);
                }
            }
        }
        return headerMap;
    }
    /**
     * 提取请求头信息 优先级 @Request(headers=?)  <  @Headers map <  @Headers(val)
     * @param proxy 代理对象
     * @param method 代理方法
     * @param args 方法参数列表
     * @return 请求头 map
     */
    default Map<String,Object> proxyHeader(Post proxy, Method method, Object[] args){
        String[] headers = proxy.headers();
        return proxyHeader(headers, method, args);
    }

    default Map<String,Object> proxyHeader(Get proxy, Method method, Object[] args){
        String[] headers = proxy.headers();
        return proxyHeader(headers, method, args);
    }

    default Map<String,Object> proxyHeader(MicroPost proxy, Method method, Object[] args){
        String[] headers = proxy.headers();
        return proxyHeader(headers, method, args);
    }

    default Map<String,Object> proxyHeader(MicroGet proxy, Method method, Object[] args){
        String[] headers = proxy.headers();
        return proxyHeader(headers, method, args);
    }
}
