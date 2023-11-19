package org.lboot.mrest.constant;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kindear
 * 服务注册发现中心定义枚举类
 */
@Deprecated
public enum RDClient {
    NACOS,
    CONSUL;



    private static final Map<String, RDClient> mappings = new HashMap<>(16);

    @Nullable
    public static RDClient resolve(@Nullable String client) {
        return client != null ? (RDClient)mappings.get(client) : null;
    }



    static {
        RDClient[] clients = values();
        for (RDClient client : clients) {
            mappings.put(client.name(), client);
        }

    }
}
