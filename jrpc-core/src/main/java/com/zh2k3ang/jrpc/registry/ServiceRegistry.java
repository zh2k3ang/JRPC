package com.zh2k3ang.jrpc.registry;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface ServiceRegistry {
    boolean registerService(String serviceName, InetSocketAddress address);
}
