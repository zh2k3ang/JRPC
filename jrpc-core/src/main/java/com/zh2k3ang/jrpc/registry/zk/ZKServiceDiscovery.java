package com.zh2k3ang.jrpc.registry.zk;

import com.zh2k3ang.jrpc.registry.loadbalance.LoadBalance;
import com.zh2k3ang.jrpc.registry.ServiceDiscovery;
import com.zh2k3ang.jrpc.registry.loadbalance.TestLoadBalance;
import com.zh2k3ang.jrpc.registry.zk.utils.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceDiscovery implements ServiceDiscovery {

    private LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        this.loadBalance = new TestLoadBalance();
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
