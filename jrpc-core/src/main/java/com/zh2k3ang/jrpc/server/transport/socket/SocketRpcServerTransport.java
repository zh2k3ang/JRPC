package com.zh2k3ang.jrpc.server.transport.socket;

import com.zh2k3ang.jrpc.server.transport.RpcServerTransport;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SocketRpcServerTransport implements RpcServerTransport {

    private final ExecutorService executor;

    public SocketRpcServerTransport() {
        this.executor = Executors.newFixedThreadPool(10);
    }

    @Override
    public void start(String host, int port) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(host, port));
            log.info("socket rpc server is listening at {}:{}", host, port);
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                log.info("accept a socket");
                executor.execute(new SocketRpcRequestHandler(socket));
            }
            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
