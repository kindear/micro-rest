package org.lboot.mrest.service;

public interface ServiceResolution {
    /**
     * 根据服务名称返回请求IP
     * @param serviceName 服务名称
     * @return 服务 IP
     */
    public String resolve(String serviceName);

    /**
     * 根据服务名称和分组返回请求IP
     * @param groupName 分组名
     * @param serviceName 服务名称
     * @return 服务 ip
     */
    public String resolve(String groupName, String serviceName);
}
