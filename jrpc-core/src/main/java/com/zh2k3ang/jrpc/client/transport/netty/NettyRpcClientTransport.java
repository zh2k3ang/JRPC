package com.zh2k3ang.jrpc.client.transport.netty;

import com.zh2k3ang.jrpc.client.transport.RpcClientTransport;
import com.zh2k3ang.jrpc.common.constant.RpcConstants;
import com.zh2k3ang.jrpc.common.enums.CompressTypeEnum;
import com.zh2k3ang.jrpc.common.enums.SerializationTypeEnum;
import com.zh2k3ang.jrpc.common.protocol.RpcMessage;
import com.zh2k3ang.jrpc.common.protocol.RpcRequest;
import com.zh2k3ang.jrpc.common.netty.codec.RpcMessageDecoder;
import com.zh2k3ang.jrpc.common.netty.codec.RpcMessageEncoder;
import com.zh2k3ang.jrpc.common.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClientTransport implements RpcClientTransport {


    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    public NettyRpcClientTransport() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
    }

    @SneakyThrows
    @Override
    public Object sendRpcRequest(RpcRequest request, InetSocketAddress addr) {

        // connect server
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(addr).addListener((ChannelFutureListener)future -> {
            if(future.isSuccess()) {
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });


        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        try {
            // get channel and message
            Channel channel = completableFuture.get();
            RpcMessage rpcMessage = RpcMessage.builder().data(request)
                    .codec(SerializationTypeEnum.PROTOSTUFF.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            // send message
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return resultFuture.get();
    }
}
