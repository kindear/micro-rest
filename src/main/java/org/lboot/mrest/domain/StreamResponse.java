package org.lboot.mrest.domain;

import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;


public class StreamResponse extends SseEmitter {
    public StreamResponse(Long timeout) {
        super(timeout);
    }

    public StreamResponse(){
        super();
    }

    @Override
    @SneakyThrows
    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);

        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType( new MediaType("text", "event-stream", StandardCharsets.UTF_8));
    }
}
