package com.zh2k3ang.jrpc.demo.client;

import com.zh2k3ang.jrpc.entities.RpcServiceProperties;
import com.zh2k3ang.jrpc.remoting.transport.RpcRequestTransport;
import com.zh2k3ang.jrpc.remoting.transport.socket.SocketRpcClient;

public class SocketRpcClientMain {

    public static void main(String[] args) {
        RpcServiceProperties properties = RpcServiceProperties.builder()
                .group("zh2k3ang").version("v1").serviceName("hello").build();
        RpcRequestTransport socketRpcClient = new SocketRpcClient();
    }
}
