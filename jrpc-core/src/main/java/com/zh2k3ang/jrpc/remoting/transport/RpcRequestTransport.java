package com.zh2k3ang.jrpc.remoting.transport;

import com.zh2k3ang.jrpc.remoting.dto.RpcRequest;

public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest request);
}
