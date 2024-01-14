# MicroRest

`MircoRest`是一款基于`okhttp`构建的 `http`客户端请求框架，优雅适配了微服务请求，它能够将 HTTP 的所有请求信息（包括 URL、Header 以及 Body 等信息）绑定到您自定义的 Interface 方法上，支持上下文穿透、请求参数动态修改，SSE请求支持，能够通过调用本地接口方法的方式发送 HTTP 请求。

其设计思想基于动态代理。

## 特性

- [x] 自动类型转化，接口返回值自动转化为函数接收对象类型(使用Map fillBean)

- [x] 传统`HTTP` 请求支持， `@Get` `@Post` `@Put` `@Delete` 支持

- [x] 文件上传支持

- [x] 微服务请求支持，`@MicroGet` `@MicroPost` `@MicroPut` `@MicroDelete` 支持，默认支持 `NacOS`，支持自定义拓展

- [x] `SSE`请求支持，`@SseGet` `@SsePost`支持，类`GPT`请求实现转发

- [x] 请求参数自定义，`@PathVar` 自定义路径参数 , `@Query` 自定义查询参数

- [x] 请求头自定义 ，`@Headers` 请求头支持，多种构建方式

- [x] 请求体自定义， `@Body` 请求体支持，多种构建方式

- [x] 自定义请求客户端 `MicroRestClient`

- [x] 支持装饰器模式 ，传递请求头和请求体

  



## 安装配置

### 安装

需要配置 `jitpack`作为镜像源，在`pom`文件中引入

```xml
<!--jitpack-->
<repositories>
   <repository>
       <id>jitpack.io</id>
       <url>https://www.jitpack.io</url>
   </repository>
</repositories>
```

引入 `micro-rest` 依赖，版本号查看右侧发行版版本

```xml
<!--微服务请求-->
<dependency>
   <groupId>com.gitee.lboot</groupId>
   <artifactId>micro-rest</artifactId>
   <version>${version}</version>
</dependency>
```

### 配置

该框架默认支持并实现了基于 `nacos`的微服务发现机制，需要配置如下

> 不需要引入 nacos 依赖，是基于 openAPI 构建实现

```properties
#################### 服务注册发现 #######################
nacos.discovery.server-addr=127.0.0.1:8848
nacos.discovery.auto-register=true
nacos.discovery.enabled=true
```

支持拓展其他的服务发现，只需要自定义拓展实现`ServiceResolution`

## 快速上手

> 开放接口提供文章： https://juejin.cn/post/7041461420818432030

对于需要自定义的接口，需要用 `@MicroRest`标记为需要动态代理实现的接口，程序编译时会自动构建对应的请求方法。对于请求返回的结果，程序会自动读取接口参数类型并构建转化函数实现数据的转化。

### 请求地址构建

构建一个请求，最重要的是请求地址的构建，如下注解实现了请求路径参数和查询参数的构建

#### @PathVar 

> 路径参数构建

`@PathVar("key")` 可以指定路径参数名称，如果没有指定，则按照当前参数实际名称进行替换，例如下图两种定义方式都可以。

```java
@MicroRest
public interface TestPostApi {
    @Get("http://jsonplaceholder.typicode.com/posts/{id}")
    PostVO getPost(@PathVar Long id);
    
    @Get("http://jsonplaceholder.typicode.com/posts/{id}")
    PostVO getPost(@PathVar("id") Long xx);
}
```



#### @Query

> 查询参数构建

其参数替换有如下规则：

1. 指定参数为基础数据类型（原子性数据，不可再分割，例如 `Integer, Long, String `），支持使用`@Query("key") ` 指定参数名称，或使用 `@Query` 使用当前参数实际名称，下面两种定义方式效果一致。

   ```java
   @MicroRest
   public interface TestPostApi {
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Query("userId") Long xx);
       
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Query Long userId);
   }
   ```

2. 指定参数非基础数据类型，例如`Map,用户自定义类`, 仅支持 `@Query`，支持自动转化参数，下面定义效果与上面一致。

   > 用户自定义查询参数

   ```java
   class QueryParams{
   	String userId;
   }
   ```

   ```java
   @MicroRest
   public interface TestPostApi {
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Query QueryParams params);
   }
   ```



#### #{}

> 模板内容替换

目前仅支持读取`application.properties`配置文件中的配置项的值，并实现替换 

> application.properties

```
openai.chat.host=https://api.openai-proxy.com
```



```java
@MicroRest
public interface MicroRestChatApi {
    @SsePost(value = "#{openai.chat.host}/v1/chat/completions", converter = ChatConverter.class)
    StreamResponse chatCompletions(@Headers Map<String, Object> headers, @Body Map<String,Object> params);

}
```



### 请求头构建

#### @Headers

> 请求头信息构建

其参数替换有如下规则：

1. 指定参数为基础数据类型（原子性数据，不可再分割，例如 `Integer, Long, String `），支持使用`@Headers("key") ` 指定参数名称，或使用 `@Headers` 使用当前参数实际名称，下面两种定义方式效果一致。

   ```java
   @MicroRest
   public interface TestPostApi {
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Headers("token") String token);
       
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Headers String token);
   }
   ```

   

2. 指定参数非基础数据类型，例如`Map,用户自定义类`, 仅支持 `@Headers`，支持自动转化参数，下面定义效果与上面一致。

   > 自定义实体类

   ```java
   class CustomHeaders{
   	String token = "xxx";
   }
   ```

   > Map

   ```json
   {
   	"Content-Type":"application/x-www-form-urlencoded"
   }
   ```

   > 使用举例

   ```java
   @MicroRest
   public interface TestPostApi {
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Headers CustomHeaders headers);
       
       @Get("http://jsonplaceholder.typicode.com/posts")
       List<PostVO> getPosts(@Headers Map<String,Object> headers);
   }
   ```

3. 原子性数据参数优先级大于自定义类或`Map`, 会覆盖其中的数据。



#### @Anno headers

例如`@Get`, `@MicroPost`等一些列注解，都支持请求头参数配置，其配置形式如下:

```java
@MicroRest
public interface TestPostApi {
    @Post(url = "http://jsonplaceholder.typicode.com/posts",
    headers = {
           "Content-Type:application/x-www-form-urlencoded;charset=utf-8"
    })
    PostVO createPost(@Headers Map<String,Object> headers);
}
```

这种方式一般用于固定数值的请求头，需要动态变化的，采用上面的方式进行组装。

支持模板替换，自动匹配替换`application.properties`指定的数据项

```java
    @SsePost(value = "#{openai.chat.host}/v1/chat/completions",
            headers = {"Authorization:Bearer #{openai.chat.key}"},
            converter = ChatConverter.class)
    StreamResponse chatCompletions(@Body Map<String,Object> params);
```

其中` headers = {"Authorization:Bearer #{openai.chat.key}"}`就是模板请求头用法

### 请求体构建

#### @Body

> 请求体构建

该参数用法和`@Query`, `@Headers` 都很相似

1. 指定参数为基础数据类型（原子性数据，不可再分割，例如 `Integer, Long, String `），支持使用`@Body("key") ` 指定参数名称，或使用 `@Body` 使用当前参数实际名称。

2. 指定参数非基础数据类型，例如`Map,用户自定义类`, 仅支持 `@Body`，支持自动转化参数。

3. 原子性数据参数优先级大于自定义类或`Map`, 会覆盖其中的数据。

   ```java
   @MicroRest
   public interface TestPostApi {
       @Post("http://jsonplaceholder.typicode.com/posts")
       Object createPost(@Body PostVO postVO);
   }
   ```



### 请求透传

#### @Decorator

> 请求透传

被该注解标记的方法，会根据配置属性，自动读取请求上下文中的请求头和请求体

```java
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decorator {
    // 是否读取请求头
    boolean withHeader() default true;
    
    // 是否读取请求体
    boolean withBody() default true;
    
    // 装饰器默认实现 --> 请求头和请求体读取,支持自定义
    Class<? extends ProxyContextDecorator> value() default DefaultProxyContextDecorator.class;
}
```



> 默认实现

```java
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
```

自定义实现需要基础该抽象类并重写



### 文件上传支持

通过对 `@Post`,`@MicroPost`请求头配置实现

1. 请求头配置为 `   Content-Type:multipart/form-data`

```java
   @Post(value = "#{openai.chat.host}/v1/files",headers = {
            "Content-Type:multipart/form-data",
            "Authorization: Bearer #{openai.chat.key}"
    })
    Map<String,Object> uploadFile(@Body ChatFileParams params);
```



```java
@Data
public class ChatFileParams {
    File file;
    
    String purpose;
}

```

如果没有其余附加信息，可以只上传文件

```java
   @Post(value = "http://localhost:8080/v1/files",headers = {
            "Content-Type:multipart/form-data",
    })
    Map<String,Object> uploadFile(@Body("fileKey") File file);
```





### REST请求支持



#### @Post

> 作用域：方法

#### @Put

> 作用域：方法

#### @Get

> 作用域：方法

#### @Delete

> 作用域：方法

#### 注解属性

上述注解注解属性一致：

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XXX {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";
	// 默认请求头配置
    String[] headers() default {};

}
```





### 微服务请求支持

#### @MicroPost

> 作用域：方法

#### @MicroPut

> 作用域：方法

#### @MicroGet

> 作用域：方法

#### @MicroDelete

> 作用域：方法

#### 注解属性

上述注解属性项一致

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MicroXXX {
    // 分组名称，不传递则为默认分组
    String groupName() default "";
    // 微服务名称
    String serviceName() default "";

    // 请求路径
    String path() default "";

    // 请求头信息
    String[] headers() default {};

}
```





#### 基础使用

项目已经基于`OpenApi`实现了对`NacOS`微服务请求的支持，指定服务名称，构建请求时，会自动将服务名称置换为对应的请求地址

```java
@MicroRest
public interface AuthApi {
    @MicroGet(serviceName = "auth-hrm",path = "/system/user/info")
    AuthInfo getUserInfo(@Headers("token") String token);


    @MicroGet(serviceName = "auth-hrm", path = "/system/users/{id}")
    List<Object> getUserById(@PathVar("id") String id);
}
```



#### 自定义服务解析

通过继承实现 `ServiceResolution` 接口，可以适配多种服务注册发现中心，以本项目默认实现举例:

[NacosServiceResolution.java](https://gitee.com/lboot/micro-rest/blob/master/src/main/java/org/lboot/mrest/nacos/NacosServiceResolution.java)



### SSE请求支持

`MicroRest`实现了对`SSE`请求的支持，限制返回类型必须为`StreamResponse`

[一文读懂即时更新方案：SSE - 掘金 (juejin.cn)](https://juejin.cn/post/7221125237500330039)

以`ChatGPT`请求转发为例

```java
@MicroRest
public interface MicroRestChatApi {
    @SsePost(value = "#{openai.chat.host}/v1/chat/completions", converter = ChatConverter.class)
    StreamResponse chatCompletions(@Headers Map<String, Object> headers, @Body Map<String,Object> params);

}
```

> Controller 写法

```java
MicroRestChatApi microRestChatApi;
@GetMapping(value = "stream/chat/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@ApiOperation(value = "流聊天")
@SneakyThrows
public StreamResponse doStreamChat(@PathVariable("chatId") String chatId){
     // 构建请求头和请求体
     return microRestChatApi.chatCompletions(headers, paramMap);
}
```

#### @SsePost

> 作用域： 方法

#### @SseGet

> 作用域：方法

#### 注解属性

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SseXXX {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    String[] headers() default {};

    // 终止信号
    String signal() default "DONE";
    
    // 信息类型转换默认实现类
    Class<? extends SseMessageConverter> converter() default DefaultSseMessageConverter.class;

}
```

#### @SseSocketId

类似 `@PathVar`用法，不需要指定内部参数

> 作用域：参数

`SSE`请求需要指定信道ID，如果没有指定则自动构建随机信道ID

```java
@MicroRest
public interface MicroRestChatApi {
    @SsePost(value = "#{openai.chat.host}/v1/chat/completions", converter = ChatConverter.class)
    StreamResponse chatCompletions(@Headers Map<String, Object> headers, @Body Map<String,Object> params, @SseSocketId String socketId);
}
```



### 请求客户端

基于`okhttp`构建请求比较复杂，基于`okhttp`封装`MircoRestClient`，可以非常方便的构建请求，实现用户自己的自定义请求。

用法举例:

```java
Response response = new MicroRestClient()
                    .url("http://localhost:8001")
                    .addQuery("serviceName",serviceName)
                    .addQuery("groupName", groupName)
                    .execute();
ResponseBody responseBody = response.body();
```



支持方法:

| 方法                              | 备注             | 返回结果         |
| --------------------------------- | ---------------- | ---------------- |
| header(Map<String,Object> header) | 设置请求头       | MicroRestClient  |
| addHeader(String key, Object val) | 设置请求头       | MicroRestClient  |
| body(Map<String,Object> body)     | 设置请求体       | MicroRestClient  |
| addBody(String key, Object val)   | 设置请求体       | MicroRestClient  |
| query(Map<String,Object> query)   | 设置请求参数     | MicroRestClient  |
| addQuery(String key, Object val)  | 设置请求参数     | MicroRestClient  |
| method(HttpMethod httpMethod)     | 设置请求方法     | MicroRestClient  |
| method(String method)             | 设置请求方法     | MicroRestClient  |
| url(String url)                   | 设置请求地址     | MicroRestClient  |
| sse()                             | 设置开启`SSE`    | MicroRestClient  |
| execute()                         | 请求执行         | Response:okhttp3 |
| getRequest()                      | 获取当前请求信息 | Request:okhttp3  |
|                                   |                  |                  |



