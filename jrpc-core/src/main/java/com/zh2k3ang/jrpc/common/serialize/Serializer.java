package com.zh2k3ang.jrpc.common.serialize;

public interface Serializer {
    byte[] serialize(Object object);
    <T> T deserialize(byte[] data, Class<T> cls);
}
