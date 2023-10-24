package org.lboot.mrest.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kindear
 * 自定义请求异常
 */
@Slf4j
public class MicroRestException extends RuntimeException{
    Integer code;

    String message;

    public MicroRestException(Integer code,String message){
        super(message);
        this.code = code;

    }
}
