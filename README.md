# MicroRest

`MircoRest`是一款基于`okhttp`构建的 `http`客户端请求框架，优雅适配了微服务请求，它能够将 HTTP 的所有请求信息（包括 URL、Header 以及 Body 等信息）绑定到您自定义的 Interface 方法上，支持上下文穿透、请求参数动态修改，能够通过调用本地接口方法的方式发送 HTTP 请求。

其设计思想基于动态代理。

## 特性

- [x] 自动类型转化，接口返回值自动转化为函数接收对象类型(使用Map fillBean)

- [x] 传统`HTTP` 请求支持， `@Get` `@Post` `@Put` `@Delete` 支持

- [x] 微服务请求支持，`@MicroGet` `@MicroPost` `@MicroPut` `@MicroDelete` 支持，默认支持 `NacOS`，支持自定义拓展

- [x] 请求参数自定义，`@PathVar` 自定义路径参数 , `@Query` 自定义查询参数

- [x] 请求头自定义 ，`@Headers` 请求头支持

- [x] 请求体自定义， `@Body` 请求体支持

- [x] 自定义请求客户端 `MicroRestClient`

- [ ] 

  



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

> 接口测试请参考：[前端需要的免费在线api接口 - 掘金 (juejin.cn)](https://juejin.cn/post/7041461420818432030)

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

#### 固定传值

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



### 请求体构建

#### @Body

> 请求体构建

该参数用法和`@Query`, `@Headers` 都很相似

1. 指定参数为基础数据类型（原子性数据，不可再分割，例如 `Integer, Long, String `），支持使用`@Body("key") ` 指定参数名称，或使用 `@Body` 使用当前参数实际名称。

2. 指定参数非基础数据类型，例如`Map,用户自定义类`, 仅支持 `@Body`，支持自动转化参数，下面定义效果与上面一致。

3. 原子性数据参数优先级大于自定义类或`Map`, 会覆盖其中的数据。

   ```java
   @MicroRest
   public interface TestPostApi {
       @Post("http://jsonplaceholder.typicode.com/posts")
       Object createPost(@Body PostVO postVO);
   }
   ```

### 微服务请求支持

#### 基础使用

基于`OpenApi`实现了对`NacOS`微服务请求的支持，指定服务名称，构建请求时，会自动将服务名称置换为对应的请求地址

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





