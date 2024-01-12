package org.lboot.mrest.test.service;

import com.lucy.chat.params.chat.ChatResult;
import org.lboot.mrest.annotation.*;
import org.lboot.mrest.test.domain.CustomDefineClass;
import org.lboot.mrest.test.domain.CustomRequestBody;
import org.lboot.mrest.test.domain.PostVO;

@MicroRest
public interface MicroRestAnnoDemo {
    @Post(url = "http://xxx",
    headers = {
            "uid:10086"
    })
    CustomDefineClass doPost(@Body CustomRequestBody body);

    @Put(url = "http://xxx",
            headers = {
                    "uid:10086"
            })
    CustomDefineClass doPut(@Body CustomRequestBody body);

    @Get(url = "http://xxx/{userId}",
            headers = {
                    "token:10086"
            })
    CustomDefineClass doGet(@PathVar String userId);
}
