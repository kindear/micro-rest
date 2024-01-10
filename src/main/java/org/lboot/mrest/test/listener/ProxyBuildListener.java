package org.lboot.mrest.test.listener;

import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.domain.ProxyBuild;
import org.lboot.mrest.event.ProxyRequestExecuteEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProxyBuildListener{
    @EventListener(ProxyRequestExecuteEvent.class)
    public void ProxyRequestExecuteEventListener(ProxyRequestExecuteEvent event){
        ProxyBuild proxyBuild = event.getProxyBuild();
        proxyBuild.print();
    }
}
