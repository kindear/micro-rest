# MicroRest

`MircoRest`是一款基于`okhttp`构建的 `http`客户端请求框架，优雅适配了微服务请求，它能够将 HTTP 的所有请求信息（包括 URL、Header 以及 Body 等信息）绑定到您自定义的 Interface 方法上，能够通过调用本地接口方法的方式发送 HTTP 请求。

其设计思想基于动态代理。

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

对于需要自定义的接口，需要用 `@MicroRest`标记为需要动态代理实现的接口，程序编译时会自动构建对应的请求方法。对于请求返回的结果，程序会自动读取接口参数类型并构建转化函数实现数据的转化。

### 请求方法构建



