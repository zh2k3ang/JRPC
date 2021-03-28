package com.zh2k3ang.jrpc.server.transport;

import com.zh2k3ang.jrpc.common.dto.RpcRequest;
import com.zh2k3ang.jrpc.common.dto.RpcResponse;
import com.zh2k3ang.jrpc.server.provider.RpcServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SocketRpcRequestHandler implements Runnable {

    private Socket socket;
    private RpcServiceProvider provider;

    public SocketRpcRequestHandler(Socket socket) {
        this.socket = socket;
        this.provider = RpcServiceProvider.getInstance();
    }

    public Object handle(RpcRequest request) {
        Object service = provider.getService(request.toRpcServiceProperties());
        Object result = null;
        try {
            Method method =service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            result = method.invoke(service, request.getParams());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            RpcRequest request =  (RpcRequest) input.readObject();
            Object result = handle(request);

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(RpcResponse.success(result, request.getRequestId()));
            output.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
