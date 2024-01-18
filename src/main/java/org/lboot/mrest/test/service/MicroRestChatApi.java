package org.lboot.mrest.test.service;


import org.lboot.mrest.annotation.*;
import org.lboot.mrest.domain.StreamResponse;
import org.lboot.mrest.test.converter.ChatConverter;
import org.lboot.mrest.test.domain.ChatFileParams;
import org.lboot.mrest.test.handler.ChatResponseHandler;

import java.util.Map;


@MicroRest
public interface MicroRestChatApi {
    @SsePost(value = "#{openai.chat.host}/v1/chat/completions",
            headers = {"Authorization: Bearer #{openai.chat.key}"},
            converter = ChatConverter.class)
    @ResponseHandler(ChatResponseHandler.class)
    StreamResponse chatCompletions(@Body Map<String,Object> params);

    @Post(value = "#{openai.chat.host}/v1/files",headers = {
            "Content-Type:multipart/form-data",
            "Authorization: Bearer #{openai.chat.key}"
    })
    Map<String,Object> uploadFile(@Body ChatFileParams params);

}
