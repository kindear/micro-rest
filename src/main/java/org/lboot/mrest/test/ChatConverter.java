package org.lboot.mrest.test;

import cn.hutool.json.JSONUtil;
import com.lucy.chat.params.chat.ChatResult;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.SseMessageConverter;

@Slf4j
public class ChatConverter extends SseMessageConverter {
    @Override
    public String convert(String jsonStr) {
        ChatResult chatResult = JSONUtil.toBean(jsonStr, ChatResult.class);
       return jsonStr;
    }


}
