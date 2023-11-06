package org.lboot.mrest.nacos.domain;

import lombok.Data;

import java.util.List;

@Data
public class ServiceInstance {

    private String name;
    private String groupName;
    private String clusters;
    private int cacheMillis;
    private List<ServiceHost> hosts;
    private long lastRefTime;
    private String checksum;
    private boolean allIPs;
    private boolean reachProtectionThreshold;
    private boolean valid;
}
