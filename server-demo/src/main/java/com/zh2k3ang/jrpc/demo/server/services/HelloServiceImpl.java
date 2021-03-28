package com.zh2k3ang.jrpc.demo.server.services;

import com.zh2k3ang.jrpc.demo.api.HelloService;

public class HelloServiceImpl implements HelloService {
    public String hello(String hello) {
        System.out.println(hello);
        return "hello";
    }

    public String bye(String bye) {
        System.out.println(bye);
        return "bye";
    }
}
