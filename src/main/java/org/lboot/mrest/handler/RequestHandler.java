package org.lboot.mrest.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.http.HttpUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kinder
 * 请求处理接口
 * 参考请求头常量类 HttpHeaders.ACCEPT etc..
 * 请求头值 MediaType.APP..
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
     * 代理请求表单
     * @param proxy 代理对象
     * @param method 代理方法
     * @param args 代理参数
     * @return 组装结果
     */
    default Map<String, Object> proxyForm(Object proxy, Method method, Object[] args){
        Parameter[] parameters = method.getParameters();
        Map<String,Object> formMap = new HashMap<>();
        // 遍历字段提取 @Body 信息
        int paramsLen = parameters.length;
        for (int i = 0; i < paramsLen; i++){
            Parameter parameter = parameters[i];
            Form form = parameter.getAnnotation(Form.class);
            if (Validator.isNotEmpty(form)){
                // 如果为空且Bean
                if (form.value().isEmpty()){
                    if (isCustomClass(args[i].getClass())){
                        Map<String,Object> beanMap = BeanUtil.beanToMap(args[i]);
                        for (String key: beanMap.keySet()){
                            if (!formMap.containsKey(key)){
                                formMap.put(key,beanMap.get(key));
                            }
                        }
                    }else if (args[i] instanceof Map){
                        // 如果是map
                        Map<String,Object> beanMap = (Map<String, Object>) args[i];
                        for (String key: beanMap.keySet()){
                            if (!formMap.containsKey(key)){
                                formMap.put(key,beanMap.get(key));
                            }
                        }
                    }

                }else {
                    // 基础变量类型
                    if (!isCustomClass(args[i].getClass())){
                        formMap.put(form.value(),args[i]);
                    }
                }
            }
        }
        return formMap;
    }

    /**
     * 代理实现请求体
     * @param proxy 代理对象
     * @param method 代理方法
     * @param args 代理参数
     * @return 组装结果
     */
    default Map<String,Object> proxyBody(Object proxy,Method method, Object[] args){
        Parameter[] parameters = method.getParameters();
        Map<String,Object> bodyMap = new HashMap<>();
        // 遍历字段提取 @Body 信息
        int paramsLen = parameters.length;
        for (int i = 0; i < paramsLen; i++){
            Parameter parameter = parameters[i];
            Body body = parameter.getAnnotation(Body.class);
            if (Validator.isNotEmpty(body)){
                // 如果为空且Bean
                if (body.value().isEmpty()){
                    if (isCustomClass(args[i].getClass())){
                        Map<String,Object> beanMap = BeanUtil.beanToMap(args[i]);
                        for (String key: beanMap.keySet()){
                            if (!bodyMap.containsKey(key)){
                                bodyMap.put(key,beanMap.get(key));
                            }
                        }
                    }else if (args[i] instanceof Map){
                        // 如果是map
                        Map<String,Object> beanMap = (Map<String, Object>) args[i];
                        for (String key: beanMap.keySet()){
                            if (!bodyMap.containsKey(key)){
                                bodyMap.put(key,beanMap.get(key));
                            }
                        }
                    }

                }else {

                    // 基础变量类型
                    if ( !isCustomClass(args[i].getClass())){
                        bodyMap.put(body.value(),args[i]);
                    }

                }
                //System.out.println(bodyMap.toString());
            }
        }
        return bodyMap;
    }


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
            // 获取部分基础信息
            Class<?> clazz = args[i].getClass();
            boolean isCustom = isCustomClass(clazz);
            Parameter parameter = parameters[i];
            // 路径参数注解
            PathVar pathVar = parameter.getAnnotation(PathVar.class);
            if (Validator.isNotEmpty(pathVar)){
                // PathVar 不支持复杂参数
                if (pathVar.value().isEmpty()){
                    pathMap.put(parameter.getName(), args[i]);
                }else {
                    pathMap.put(pathVar.value(), args[i]);
                }
            }
            // 请求参数注解
            Query query = parameter.getAnnotation(Query.class);
            if (Validator.isNotEmpty(query)){

                // @Query 没有指定值
                if (query.value().isEmpty()){
                    // 如果是 Bean ，转化为 Map
                    if (isCustom){
                        Map<String,Object> beanMap = BeanUtil.beanToMap(args[i]);
                        queryMap.putAll(beanMap);
                    }else {
                        // 如果不是，提取参数名
                        queryMap.put(parameter.getName(), args[i]);
                    }


                }else {
                    if (!isCustom){
                        queryMap.put(query.value(), args[i]);
                    }

                }
            }
        }
        // 读取配置文件替换模糊
        url = buildByProps(url);
        // 替换地址
        for (String pathKey:pathMap.keySet()){
            url = url.replace("{" + pathKey + "}" , pathMap.get(pathKey).toString());
        }
        //工具类方法
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
     * 读取配置文件替换模板内容
     * @param str
     * @return
     */
    public static String buildByProps(String str){
        // 构建对象
        Props props = new Props("application.properties");
        // 提取 #{}
        // 定义正则表达式
        String regex = "#\\{([^}]*)\\}";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(str);
        // 构建返回结果
        String resultStr = str;
        // 查找匹配的内容
        while (matcher.find()) {
            String replaceText = matcher.group(0);
            String replaceKey = matcher.group(1);
            // System.out.println(replaceText +"|"+ replaceKey);
            resultStr = resultStr.replace(replaceText, props.getProperty(replaceKey));
        }
        return resultStr;
    }


    /**
     * 请求头信息默认组装方法 @Headers(val) > @Headers 具名优先级更高
     * @param headers 请求头[]
     * @param method 方法名词
     * @param args 参数名词
     * @return 请求头 map
     */
    default Map<String,Object> proxyHeader(String []headers, Method method, Object[] args){
        // 提取
        Map<String,Object> headerMap = new HashMap<>();
        // 处理请求头默认配置
        for (String header:headers){
            String[] parts = header.split(":");
            if (parts.length == 2){
                headerMap.put(parts[0],parts[1]);
            }
        }
        Parameter[] parameters = method.getParameters();
        // 遍历字段提取 @Headers 信息
        int paramsLen = parameters.length;
        for (int i = 0; i < paramsLen; i++){
            Parameter parameter = parameters[i];
            Headers headerAnno = parameter.getAnnotation(Headers.class);
            if (Validator.isNotEmpty(headerAnno)){
                if (headerAnno.value().isEmpty()){
                    if (isCustomClass(args[i].getClass()) || args[i] instanceof Map){
                        Map<String,Object> beanMap = BeanUtil.beanToMap(args[i]);
                        for (String key: beanMap.keySet()){
                            if (!headerMap.containsKey(key)){
                                headerMap.put(key,beanMap.get(key));
                            }
                        }
                    }
                }else {
                    if (!isCustomClass(args[i].getClass())){
                        headerMap.put(headerAnno.value(),args[i]);
                    }
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
