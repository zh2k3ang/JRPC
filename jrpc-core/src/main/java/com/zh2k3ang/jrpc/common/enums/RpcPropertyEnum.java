package com.zh2k3ang.jrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcPropertyEnum {

    PRC_CONFIG_PATH("jrpc.properties"),
    ZK_ADDRESS("jrpc.zookeeper.address");
    private final String propertyValue;
}
