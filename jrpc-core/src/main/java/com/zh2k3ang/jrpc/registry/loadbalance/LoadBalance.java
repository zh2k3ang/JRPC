package com.zh2k3ang.jrpc.registry.loadbalance;

import java.util.List;

public interface LoadBalance {
    String selectServiceAddress(List<String> addresses, String serviceName);
}
