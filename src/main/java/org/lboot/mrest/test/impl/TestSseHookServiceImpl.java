package org.lboot.mrest.test.impl;

import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.loader.SseHookService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestSseHookServiceImpl implements SseHookService {
    @Override
    public void onConnect(String socketId) {
        log.info("芜湖");
    }

    @Override
    public void onError(String socketId) {

    }

    @Override
    public void onCompletion(String socketId, String msg) {
        log.info("起飞");
    }
}
