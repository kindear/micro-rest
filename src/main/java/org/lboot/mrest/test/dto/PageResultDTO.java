package org.lboot.mrest.test.dto;

import lombok.Data;

@Data
public class PageResultDTO <T>{
    Integer code;
    String msg;

    Boolean success;

    T data;
}
