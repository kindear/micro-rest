package org.lboot.mrest.test.service;


import com.lucy.chat.params.chat.ChatParams;
import com.lucy.chat.params.chat.ChatResult;
import org.lboot.mrest.annotation.*;

import java.util.Map;


@MicroRest
public interface MicroRestChatApi {
    @Post("#{openai.chat.host}/v1/chat/completions")
    ChatResult chatCompletions(@Headers Map<String, Object> headers, @Body ChatParams params);

}
