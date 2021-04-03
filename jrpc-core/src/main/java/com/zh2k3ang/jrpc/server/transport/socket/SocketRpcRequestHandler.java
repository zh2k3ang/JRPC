package com.zh2k3ang.jrpc.server.transport.socket;

import com.zh2k3ang.jrpc.common.protocol.RpcRequest;
import com.zh2k3ang.jrpc.common.protocol.RpcResponse;
import com.zh2k3ang.jrpc.server.provider.RpcServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SocketRpcRequestHandler implements Runnable {

    private Socket socket;

    public SocketRpcRequestHandler(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            RpcRequest request =  (RpcRequest) input.readObject();
            Object result = RpcServiceProvider.getInstance().handle(request);

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(RpcResponse.success(result, request.getRequestId()));
            output.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
