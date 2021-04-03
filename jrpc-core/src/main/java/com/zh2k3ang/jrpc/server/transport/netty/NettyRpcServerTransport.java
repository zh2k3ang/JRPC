package com.zh2k3ang.jrpc.server.transport.netty;

import com.zh2k3ang.jrpc.server.transport.RpcServerTransport;
import com.zh2k3ang.jrpc.common.netty.codec.RpcMessageDecoder;
import com.zh2k3ang.jrpc.common.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.TimeUnit;

public class NettyRpcServerTransport implements RpcServerTransport {


    @Override
    public void start(String host, int port) {

        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup handlerGroup = new DefaultEventExecutorGroup(5);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // socket channel will be dispatch to parent group
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(handlerGroup, new NettyRpcRequestHandler());
                        }

                    });

            ChannelFuture future = bootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            handlerGroup.shutdownGracefully();
        }

    }
}
