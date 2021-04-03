package com.zh2k3ang.jrpc.common.protocol;

import com.zh2k3ang.jrpc.common.entities.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public RpcServiceProperties toRpcServiceProperties() {
        return RpcServiceProperties.builder()
                .group(this.group)
                .version(this.version)
                .serviceName(this.interfaceName).build();
    }
}
