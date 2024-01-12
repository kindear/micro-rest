package org.lboot.mrest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lboot.mrest.service.SseMessageConverter;

@Slf4j
public class DefaultSseMessageConverter extends SseMessageConverter {
    @Override
    public String convert(String jsonStr) {
        return super.convert(jsonStr);
    }

    @Override
    public String convert(Object obj) {
        return super.convert(obj);
    }
}
