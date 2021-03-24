package com.zh2k3ang.jrpc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
public class RpcServiceProperties {
    private String group;
    private String version;
    private String serviceName;

    public String toRpcServiceName() {
        return new StringBuilder().append(this.group)
                .append('.').append(this.version)
                .append('.').append(this.serviceName)
                .toString();
    }
}
