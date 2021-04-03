package com.zh2k3ang.jrpc.client.proxy;

import com.zh2k3ang.jrpc.client.transport.netty.NettyRpcClientTransport;
import com.zh2k3ang.jrpc.client.transport.RpcClientTransport;
import com.zh2k3ang.jrpc.client.loadbalance.LoadBalance;
import com.zh2k3ang.jrpc.common.protocol.RpcRequest;
import com.zh2k3ang.jrpc.common.protocol.RpcResponse;
import com.zh2k3ang.jrpc.common.entities.RpcServiceProperties;
import com.zh2k3ang.jrpc.client.discovery.ServiceDiscovery;
import com.zh2k3ang.jrpc.client.discovery.ZKServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;

@Slf4j
public class RpcServiceProxy implements InvocationHandler {

    private final RpcServiceProperties serviceProperties;
    private final RpcClientTransport transport;
    private final ServiceDiscovery serviceDiscovery;

    public RpcServiceProxy(RpcServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
//        this.transport = new SocketRpcClientTransport();
        this.transport = new NettyRpcClientTransport();
        this.serviceDiscovery = new ZKServiceDiscovery();
    }

    public RpcServiceProxy(RpcServiceProperties serviceProperties, RpcClientTransport transport) {
        this.serviceProperties = serviceProperties;
        this.transport = transport;
        this.serviceDiscovery = new ZKServiceDiscovery();
    }

    public RpcServiceProxy(RpcServiceProperties serviceProperties, RpcClientTransport transport, LoadBalance loadBalance) {
        this.serviceProperties = serviceProperties;
        this.transport = transport;
        this.serviceDiscovery = new ZKServiceDiscovery(loadBalance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> cls) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .params(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(serviceProperties.getGroup())
                .version(serviceProperties.getVersion())
                .build();
        serviceProperties.setServiceName(rpcRequest.getInterfaceName());
        InetSocketAddress addr = this.serviceDiscovery.lookupService(serviceProperties.toRpcServiceName());
        if(addr == null) {
            log.info("no available service");
            return null;
        }
        log.info("service address found: {}", addr.toString());
        RpcResponse<Object> response = (RpcResponse<Object>) transport.sendRpcRequest(rpcRequest, addr);
        log.info("received response, {}", response.getMessage());
        return response.getData();
    }
}
