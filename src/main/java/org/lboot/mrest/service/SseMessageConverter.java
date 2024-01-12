package org.lboot.mrest.service;

import cn.hutool.json.JSONUtil;

/**
 * @author kindear
 * 推送消息转化器
 */
public abstract class SseMessageConverter {
    public String convert(String jsonStr){
        return jsonStr;
    }

    public String convert(Object obj){
        String jsonStr = JSONUtil.toJsonStr(obj);
        return convert(jsonStr);
    }
}
