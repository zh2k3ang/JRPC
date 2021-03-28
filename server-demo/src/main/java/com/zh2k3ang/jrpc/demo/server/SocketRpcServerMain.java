package com.zh2k3ang.jrpc.demo.server;

import com.zh2k3ang.jrpc.common.entities.RpcServiceProperties;
import com.zh2k3ang.jrpc.demo.api.HelloService;
import com.zh2k3ang.jrpc.demo.server.services.HelloServiceImpl;
import com.zh2k3ang.jrpc.server.provider.RpcServiceProvider;

public class SocketRpcServerMain {

    public static void main(String[] args) {

        HelloService service = new HelloServiceImpl();
        RpcServiceProperties properties = RpcServiceProperties
                .builder().group("zh2k3ang").version("v1").build();
        RpcServiceProvider provider = RpcServiceProvider.getInstance();
        provider.publishService(service, properties);
        provider.start("127.0.0.1", 8000);
    }
}
