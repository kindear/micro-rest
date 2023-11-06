package org.lboot.mrest.nacos.domain;

import lombok.Data;

@Data
public class ServiceHost {
    private String ip;
    private int port;
    private int weight;
    private boolean healthy;
    private boolean enabled;
    private boolean ephemeral;
    private String clusterName;
    private String serviceName;
    private String metadata;
    private int instanceHeartBeatTimeOut;
    private int ipDeleteTimeout;
    private int instanceHeartBeatInterval;
}
