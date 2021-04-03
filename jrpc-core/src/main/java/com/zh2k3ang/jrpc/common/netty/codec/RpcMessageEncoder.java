package com.zh2k3ang.jrpc.common.netty.codec;

import com.zh2k3ang.jrpc.common.constant.RpcConstants;
import com.zh2k3ang.jrpc.common.enums.CompressTypeEnum;
import com.zh2k3ang.jrpc.common.enums.SerializationTypeEnum;
import com.zh2k3ang.jrpc.common.protocol.RpcMessage;
import com.zh2k3ang.jrpc.common.serialize.KryoSerializer;
import com.zh2k3ang.jrpc.common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * <p>
 * custom protocol decoder
 * <p>
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        try {
            // 填充网络协议前面规定的字节
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcConstants.VERSION);
            byteBuf.writerIndex(byteBuf.writerIndex()+4);
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getCodec());
            byteBuf.writeByte(CompressTypeEnum.GZIP.getCode());
            byteBuf.writeInt(count.getAndIncrement());
            byteBuf.release();

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // 序列化RpcRequest对象
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = new KryoSerializer();
                bodyBytes = serializer.serialize(rpcMessage.getData());
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }
            int writeIndex = byteBuf.writerIndex();
            // 把前面空出来的长度填充进去
            byteBuf.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
        } catch(Exception e) {
            log.error("Encode request error!", e);
        }
    }
}
