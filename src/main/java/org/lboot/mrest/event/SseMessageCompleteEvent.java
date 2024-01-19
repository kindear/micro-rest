package org.lboot.mrest.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.impl.SseServiceImpl;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * @author kindear
 * SSE 消息完成
 */
@Slf4j
@Getter
public class SseMessageCompleteEvent extends ApplicationEvent {
    String msg;
    public SseMessageCompleteEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public SseMessageCompleteEvent(Object source, Clock clock) {
        super(source, clock);
    }


}
