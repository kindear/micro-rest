package org.lboot.mrest.test.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
public class ChatMulFileParams {
    MultipartFile file;

    @ApiModelProperty(value = "目的",example = "assistants")
    String purpose;
}
