package org.lboot.mrest.test;

import com.lucy.chat.dto.ChatDTO;
import com.lucy.chat.params.chat.ChatMessage;
import com.lucy.chat.params.chat.ChatParams;
import com.lucy.chat.params.chat.ChatResult;
import com.lucy.chat.service.ChatCacheService;
import com.lucy.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.test.service.MicroRestChatApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("openapi")
@Api(tags = "GPT测试接口")
public class ChatTestController {
    @Value("${openai.chat.key}")
    private String chatKey;

    @Resource
    MicroRestChatApi microRestChatApi;

    @Autowired
    ChatService chatService;

    @Autowired
    ChatCacheService cacheService;



    @PostMapping("chat")
    @ApiOperation(value = "聊天",notes = "")
    public Object doChat(@RequestBody ChatDTO dto){
        Map<String,Object> headers = new HashMap<>();
        headers.put("Authorization","Bearer " + chatKey);
        ChatParams params = new ChatParams();

        List<ChatMessage> messages = cacheService.history(dto.getChatId(), dto.getWithContext());
        // 构建用户信息
        ChatMessage userMsg = chatService.buildUserMessage(dto.getContent());
        messages.add(userMsg);
        params.setMessages(messages);
        log.info(headers.toString());
        return microRestChatApi.chatCompletions(headers, params);
    }


}
