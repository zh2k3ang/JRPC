package com.zh2k3ang.jrpc.remoting.transport.socket;

import com.zh2k3ang.jrpc.enums.RpcErrorMessageEnum;
import com.zh2k3ang.jrpc.exceptions.RpcException;
import com.zh2k3ang.jrpc.registry.ServiceDiscovery;
import com.zh2k3ang.jrpc.remoting.dto.RpcRequest;
import com.zh2k3ang.jrpc.remoting.transport.RpcRequestTransport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = null;
    }

    public Object sendRpcRequest(RpcRequest request) {
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(
                request.toRpcServiceProperties().toRpcServiceName());

        try {
            Socket socket = new Socket();
            socket.connect(inetSocketAddress);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(request);
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException(RpcErrorMessageEnum.CLIENT_CONNECT_ERROR);
        }

    }
}
