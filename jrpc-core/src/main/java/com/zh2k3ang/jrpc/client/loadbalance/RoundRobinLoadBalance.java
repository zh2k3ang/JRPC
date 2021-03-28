package com.zh2k3ang.jrpc.client.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance implements LoadBalance {

    private static AtomicInteger index = new AtomicInteger(0);

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName) {
        int cur = index.getAndIncrement();
        if(cur>=serviceAddresses.size()) {
            cur = 0;
            index.set(0);
        }
        return serviceAddresses.get(cur);
    }
}
