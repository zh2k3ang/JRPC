package com.zh2k3ang.jrpc.common.serialize;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zh2k3ang.jrpc.common.protocol.RpcMessage;
import com.zh2k3ang.jrpc.common.protocol.RpcRequest;
import com.zh2k3ang.jrpc.common.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class KryoSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = new Kryo();
            kryo.register(RpcResponse.class);
            kryo.register(RpcRequest.class);
            kryo.writeObject(output, obj);
            return output.toBytes();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = new Kryo();
            kryo.register(RpcMessage.class);
            kryo.register(RpcRequest.class);
            Object o = kryo.readObject(input, cls);
            return cls.cast(o);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("deserialization failed");
        }
    }
}
