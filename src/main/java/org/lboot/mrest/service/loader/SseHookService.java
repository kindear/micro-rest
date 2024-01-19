package org.lboot.mrest.service.loader;

/**
 * @author kindear
 * SSE 通信监听服务
 */
public interface SseHookService {
    /**
     * 监听连接状态做对应处理
     * @param socketId
     */
    void onConnect(String socketId);

    /**
     * 监听连接错误状态做处理
     * @param socketId
     */
    void onError(String socketId);

    /**
     * 监听完成状态做对应处理
     * @param socketId
     * @param msg
     */
    void onCompletion(String socketId,String msg);

}
