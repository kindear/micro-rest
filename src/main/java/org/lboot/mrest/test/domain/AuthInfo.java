package org.lboot.mrest.test.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "鉴权返回数据")
public class AuthInfo {
    String userId;

    String userName;

    String nickName;

    Boolean isAdmin;

    Boolean isBan;

    List<String> permissions;

    List<String> roles;
}
