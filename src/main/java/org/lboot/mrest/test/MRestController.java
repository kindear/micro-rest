package org.lboot.mrest.test;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mrest")
@Api(tags = "接口测试ctl")
@AllArgsConstructor
public class MRestController {

}
