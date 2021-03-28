package com.zh2k3ang.jrpc.server.provider;

import com.zh2k3ang.jrpc.common.entities.RpcServiceProperties;
import com.zh2k3ang.jrpc.common.enums.RpcErrorMessageEnum;
import com.zh2k3ang.jrpc.common.exceptions.RpcException;
import com.zh2k3ang.jrpc.registry.ServiceRegistry;
import com.zh2k3ang.jrpc.registry.zk.ZKServiceRegistry;
import com.zh2k3ang.jrpc.server.transport.RpcServerTransport;
import com.zh2k3ang.jrpc.server.transport.SocketRpcServerTransport;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServiceProvider {

    private static Map<String, Object> serviceMap;
    private static ServiceRegistry serviceRegistry;
    private static RpcServerTransport server;


    private static class ClassHolder {
        private static final RpcServiceProvider INSTANCE = new RpcServiceProvider();
    }

    private RpcServiceProvider() {
        server = new SocketRpcServerTransport();
        serviceMap = new ConcurrentHashMap<>();
        serviceRegistry = new ZKServiceRegistry();
    }

    public static RpcServiceProvider getInstance() {
        return ClassHolder.INSTANCE;
    }

    private void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {
        String serviceName = rpcServiceProperties.toRpcServiceName();
        if(serviceMap.containsKey(serviceName)) return;
        serviceMap.put(serviceName, service);
    }

    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = serviceMap.get(rpcServiceProperties.toRpcServiceName());
        if(null == service) throw new RpcException(RpcErrorMessageEnum.SERVICE_NOT_FOUND);
        return service;
    }

    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        Class<?> serviceInterface  = service.getClass().getInterfaces()[0];
        String serviceName = serviceInterface.getCanonicalName();
        rpcServiceProperties.setServiceName(serviceName);
        this.addService(service, serviceInterface, rpcServiceProperties);
        serviceRegistry.registerService(rpcServiceProperties.toRpcServiceName(), new InetSocketAddress("127.0.0.1", 8000));
    }

    public void start(String ip, int port) {
        server.start(ip, port);
    }
}
