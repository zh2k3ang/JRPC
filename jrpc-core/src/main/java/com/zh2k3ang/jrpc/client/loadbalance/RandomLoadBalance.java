package com.zh2k3ang.jrpc.client.loadbalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> addresses, String serviceName) {
        return addresses.get(new Random().nextInt(addresses.size()));
    }
}
