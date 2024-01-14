package org.lboot.mrest.test;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
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
import org.lboot.mrest.annotation.Post;
import org.lboot.mrest.client.MicroRestClient;
import org.lboot.mrest.domain.StreamResponse;
import org.lboot.mrest.service.SseService;
import org.lboot.mrest.test.domain.ChatFileParams;
import org.lboot.mrest.test.domain.FileUploadParams;
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
import java.io.File;
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

        return microRestChatApi.chatCompletions(paramMap);
    }

    @PostMapping("chat/file")
    @ApiOperation(value = "文件上传")
    @SneakyThrows
    public Object uploadFile(){
        File file = FileUtil.file("D:\\test.docx");
        ChatFileParams fileParams = new ChatFileParams();
        fileParams.setFile(file);
        fileParams.setPurpose("assistants");
        return microRestChatApi.uploadFile(fileParams);

    }


}
