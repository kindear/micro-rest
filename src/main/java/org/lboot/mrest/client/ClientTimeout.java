package org.lboot.mrest.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kindear
 * 请求客户端超时配置
 */
@Slf4j
@Data
@ApiModel(value = "客户端超时配置")
public class ClientTimeout {
    @ApiModelProperty(value = "连接超时(s)")
    private Integer connectTimeout;

    @ApiModelProperty(value = "读取超时(s)")
    private Integer readTimeout;

    @ApiModelProperty(value = "写入超时(s)")
    private Integer writeTimeout;

    public ClientTimeout(Integer connectTimeout, Integer readTimeout, Integer writeTimeout){
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }

    public ClientTimeout(){
        setConnectTimeout(10);
        setReadTimeout(10);
        setWriteTimeout(10);
    }

}
