package com.zh2k3ang.jrpc.client.transport.socket;

import com.zh2k3ang.jrpc.client.transport.RpcClientTransport;
import com.zh2k3ang.jrpc.common.protocol.RpcRequest;
import com.zh2k3ang.jrpc.common.enums.RpcErrorMessageEnum;
import com.zh2k3ang.jrpc.common.exceptions.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketRpcClientTransport implements RpcClientTransport {

    @Override
    public Object sendRpcRequest(RpcRequest request, InetSocketAddress addr) {
        try (Socket socket = new Socket()) {
            socket.connect(addr);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // Send data to the server through the output stream
            objectOutputStream.writeObject(request);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException(RpcErrorMessageEnum.CLIENT_CONNECT_ERROR);
        }
    }
}
