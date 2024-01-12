package org.lboot.mrest.test.service;


import com.lucy.chat.params.chat.ChatParams;
import com.lucy.chat.params.chat.ChatResult;
import org.lboot.mrest.annotation.*;
import org.lboot.mrest.domain.StreamResponse;
import org.lboot.mrest.test.ChatConverter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;


@MicroRest
public interface MicroRestChatApi {
    @SsePost(value = "#{openai.chat.host}/v1/chat/completions", converter = ChatConverter.class)
    StreamResponse chatCompletions(@Headers Map<String, Object> headers, @Body Map<String,Object> params);

}
