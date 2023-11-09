package org.lboot.mrest.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.domain.ProxyBuild;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Slf4j
@Getter
public class ProxyRequestExecuteEvent extends ApplicationEvent {
    ProxyBuild proxyBuild;
    public ProxyRequestExecuteEvent(Object source, ProxyBuild proxyBuild) {
        super(source);
        this.proxyBuild = proxyBuild;
    }

    public ProxyRequestExecuteEvent(Object source, Clock clock, ProxyBuild proxyBuild) {
        super(source, clock);
        this.proxyBuild = proxyBuild;
    }
}
