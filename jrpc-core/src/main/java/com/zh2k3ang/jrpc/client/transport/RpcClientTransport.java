package com.zh2k3ang.jrpc.client.transport;

import com.zh2k3ang.jrpc.common.dto.RpcRequest;

import java.net.InetSocketAddress;

public interface RpcClientTransport {
    Object sendRpcRequest(RpcRequest request, InetSocketAddress addr);
}
