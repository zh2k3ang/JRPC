package com.zh2k3ang.jrpc.common.entities;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RpcServiceProperties {
    private String group;
    private String version;
    private String serviceName;

    public String toRpcServiceName() {
        return serviceName+"."+group+"."+version;
    }
}
