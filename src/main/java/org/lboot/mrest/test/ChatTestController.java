package org.lboot.mrest.test;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.lucy.chat.dto.ChatDTO;
import com.lucy.chat.params.chat.ChatMessage;
import com.lucy.chat.params.chat.ChatParams;
import com.lucy.chat.service.ChatCacheService;
import com.lucy.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.lboot.mrest.client.MicroRestClient;
import org.lboot.mrest.domain.StreamResponse;
import org.lboot.mrest.service.SseService;
import org.lboot.mrest.test.service.MicroRestChatApi;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Autowired
    SseService sseService;



//    @PostMapping("chat")
//    @ApiOperation(value = "聊天",notes = "")
//    public Object doChat(@RequestBody ChatDTO dto){
//        Map<String,Object> headers = new HashMap<>();
//        headers.put("Authorization","Bearer " + chatKey);
//        ChatParams params = new ChatParams();
//
//        List<ChatMessage> messages = cacheService.history(dto.getChatId(), dto.getWithContext());
//        // 构建用户信息
//        ChatMessage userMsg = chatService.buildUserMessage(dto.getContent());
//        messages.add(userMsg);
//        params.setMessages(messages);
//        log.info(headers.toString());
//        return microRestChatApi.chatCompletions(headers, params);
//    }

//    @ApiOperation(value = "SSE测试")
//    @GetMapping(path = "/connect/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter buildSSE(@PathVariable("chatId") String chatId) {
//        SseEmitter sseEmitter = sseService.connect(chatId);
//        ChatMessage message = new ChatMessage();
//        message.setRole("user");
//        message.setContent("Help me write an introduction about China in 200 words.");
//        ChatParams params = new ChatParams();
//        List<ChatMessage> messages = new ArrayList<>();
//        messages.add(message);
//        params.setMessages(messages);
//        Map<String,Object> paramMap = BeanUtil.beanToMap(params);
//        paramMap.put("stream",true);
//
////        Response response = client.newCall(request).execute();
//        Response response = new MicroRestClient()
//                .url("https://api.openai-proxy.com/v1/chat/completions")
//                .body(paramMap)
//                .sse()
//                .method(HttpMethod.POST)
//                .addHeader("Authorization","Bearer " + "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")
//                .execute();
//
//        sseService.proxy(chatId, response, "[DONE]");
//        return sseEmitter;
//
//    }
    // 请求成功

    @GetMapping(value = "stream/chat/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation(value = "流聊天")
    @SneakyThrows
    public StreamResponse doStreamChat(@PathVariable("chatId") String chatId){
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent("南唐后主是谁，有什么作品");
        ChatParams params = new ChatParams();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(message);
        params.setMessages(messages);
        Map<String,Object> paramMap = BeanUtil.beanToMap(params);
        paramMap.put("stream",true);
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization","Bearer " + "sk-");

        return microRestChatApi.chatCompletions(headers, paramMap);

//        Response response = client.newCall(request).execute();
//        Response response = new MicroRestClient()
//                .url("https://api.openai-proxy.com/v1/chat/completions")
//                .body(paramMap)
//                .sse()
//                .method(HttpMethod.POST)
//                .addHeader("Authorization","Bearer " + "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")
//                .execute();
//
//        sseService.proxy(chatId, response, "[DONE]");
    }


}
