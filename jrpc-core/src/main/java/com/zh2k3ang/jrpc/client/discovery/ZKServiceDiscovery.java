package com.zh2k3ang.jrpc.client.discovery;

import com.zh2k3ang.jrpc.client.loadbalance.ConsistentHashLoadBalance;
import com.zh2k3ang.jrpc.client.loadbalance.LoadBalance;
import com.zh2k3ang.jrpc.client.loadbalance.RandomLoadBalance;
import com.zh2k3ang.jrpc.client.loadbalance.RoundRobinLoadBalance;
import com.zh2k3ang.jrpc.common.zk.CuratorUtil;
import com.zh2k3ang.jrpc.server.registry.ZKServiceRegistry;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceDiscovery implements ServiceDiscovery {

    private LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        this.loadBalance = new ConsistentHashLoadBalance();
    }

    public ZKServiceDiscovery(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {

        CuratorFramework client = CuratorUtil.getClient();
        List<String> services = CuratorUtil.getChildrenNodes(client, ZKServiceRegistry.REGISTRY_ROOT+"/"+serviceName);
        if(services == null || services.size() == 0) return null;
        String address = loadBalance.selectServiceAddress(services, serviceName);
        String host = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);
        return new InetSocketAddress(host, port);
    }
}
