package com.zh2k3ang.jrpc.client.loadbalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance implements LoadBalance {

    private static final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;
        public ConsistentHashSelector(List<String> invokers, int replicaNum) {
            this.virtualInvokers = new TreeMap<>();

            for(String invoker : invokers) {
                for(int i=0; i<replicaNum/4; i++) {
                    byte[] digest = md5(invoker + i);
                    for(int h=0; h<4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        public String select(String serviceName) {
            byte[] digest = md5(serviceName);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();
            if(entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

    }

    @Override
    public String selectServiceAddress(List<String> addresses, String serviceName) {

        ConsistentHashSelector selector = selectors.get(serviceName);
        if(selector == null) {
            selectors.put(serviceName, new ConsistentHashSelector(addresses, 160));
            selector = selectors.get(serviceName);
        }

        return selector.select(serviceName);
    }
}
