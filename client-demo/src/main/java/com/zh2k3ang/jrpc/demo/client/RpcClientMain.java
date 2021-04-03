package com.zh2k3ang.jrpc.demo.client;

import com.zh2k3ang.jrpc.client.proxy.RpcServiceProxy;
import com.zh2k3ang.jrpc.common.entities.RpcServiceProperties;
import com.zh2k3ang.jrpc.demo.api.HelloService;

public class RpcClientMain {

    public static void main(String[] args) {
        RpcServiceProperties properties = RpcServiceProperties.builder()
                .group("zh2k3ang").version("v1").build();
        RpcServiceProxy proxy = new RpcServiceProxy(properties);
        HelloService helloService = proxy.getProxy(HelloService.class);
        String res = helloService.hello("nice to meet you");
        System.out.println(res);
    }
}
