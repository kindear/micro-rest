package org.lboot.mrest.test.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author kindear
 */
@Data
@ApiModel(value = "文件上传参数")
public class FileUploadParams {

    @ApiModelProperty("文件")
    private MultipartFile file;
}
