package com.zh2k3ang.jrpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcErrorMessageEnum {
    CLIENT_CONNECT_ERROR("failed to connect server"),
    SERVICE_INVOCATION_ERROR("failed to invoke service"),
    SERVICE_NOT_FOUND("service not found"),
    SERVICE_NOT_IMPLEMENT("service not implement"),
    REQUEST_NOT_MATCH_RESPONSE("request not match response");
    private final String message;
}
