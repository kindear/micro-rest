package org.lboot.mrest.test.listener;

import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.lboot.mrest.event.SseMessageCompleteEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MicroRestTestListener {
    @EventListener(ProxyRequestExecuteEvent.class)
    public void ProxyRequestExecuteEventListener(ProxyRequestExecuteEvent event){
        ProxyBuild proxyBuild = event.getProxyBuild();
        proxyBuild.print();
    }

    @EventListener(SseMessageCompleteEvent.class)
    public void SseMessageCompleteEventListener(SseMessageCompleteEvent event){
        String msg = event.getMsg();
        log.info("SSE传送消息为:{}", msg);
    }
}
