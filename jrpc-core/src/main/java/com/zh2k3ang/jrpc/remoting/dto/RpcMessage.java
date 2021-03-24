package com.zh2k3ang.jrpc.remoting.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    private byte messageType;
    private byte codec;
    private byte compress;
    private int requestId;
    private Object data;
}
