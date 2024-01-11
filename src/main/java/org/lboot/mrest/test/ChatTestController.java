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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.lboot.mrest.client.MicroRestClient;
import org.lboot.mrest.test.service.MicroRestChatApi;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // 请求成功

    @PostMapping("stream/chat")
    @ApiOperation(value = "流聊天")
    @SneakyThrows
    public void doStreamChat(){
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//        JSONObject data = new JSONObject();
//        data.put("messages", new JSONArray()
//                .put(new JSONObject()
//                        .put("role", "system")
//                        .put("content", "You are an AI assistant that helps people find information."))
//                .put(new JSONObject()
//                        .put("role", "user")
//                        .put("content", "为我写一篇200字左右关于南唐后主的介绍")));
//        data.put("temperature", 0.5);
//        data.put("model","gpt-3.5-turbo");
//        data.put("top_p", 0.95);
//        data.put("frequency_penalty", 0);
//        data.put("presence_penalty", 0);
//        data.put("max_tokens", 800);
//        data.put("stop", "null");
//        data.put("stream", true);
//        log.info(data.toString());
//        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), data.toString());
//        Request request = new Request.Builder()
//                .url("https://api.openai-proxy.com/v1/chat/completions")
//                .post(requestBody)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization","Bearer " + "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")
//                .build();
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent("南唐后主是谁，有什么作品");
        ChatParams params = new ChatParams();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(message);
        params.setMessages(messages);
        Map<String,Object> paramMap = BeanUtil.beanToMap(params);
        paramMap.put("stream",true);

//        Response response = client.newCall(request).execute();
        Response response = new MicroRestClient()
                .url("https://api.openai-proxy.com/v1/chat/completions")
                .body(paramMap)
                .sse()
                .method(HttpMethod.POST)
                .addHeader("Authorization","Bearer " + "xxx")
                .execute();
        String line;
        if (response.isSuccessful()){
            log.info("请求成功");
        }else {
            log.info(response.message());
            log.info(response.body().string());
        }
        while ((line = response.body().source().readUtf8Line()) != null) {
            if (line.equals("data: [DONE]")) {
                System.out.println("\n[DONE]");

                log.info(response.body().string());
                response.close();
                log.info("请求关闭");
                break;
            } else if (line.startsWith("data: ")) {
                line = line.substring(6);
                JSONObject responseJson = new JSONObject(line);
                if (responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("delta").has("content")) {
                    System.out.print(responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content"));
                }
            }
        }
    }


}
