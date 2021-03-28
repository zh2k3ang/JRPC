package com.zh2k3ang.jrpc.registry.loadbalance;

import java.util.List;

public class TestLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> addresses, String serviceName) {
        return addresses.get(0);
    }
}
