package com.zh2k3ang.jrpc.server.transport.netty;

import com.zh2k3ang.jrpc.common.constant.RpcConstants;
import com.zh2k3ang.jrpc.common.enums.CompressTypeEnum;
import com.zh2k3ang.jrpc.common.enums.RpcResponseCodeEnum;
import com.zh2k3ang.jrpc.common.enums.SerializationTypeEnum;
import com.zh2k3ang.jrpc.common.protocol.RpcMessage;
import com.zh2k3ang.jrpc.common.protocol.RpcRequest;
import com.zh2k3ang.jrpc.common.protocol.RpcResponse;
import com.zh2k3ang.jrpc.server.provider.RpcServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.KYRO.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    // Execute the target method (the method the client needs to execute) and return the method result
                   Object result = RpcServiceProvider.getInstance().handle(rpcRequest);
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }
}
