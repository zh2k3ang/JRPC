package com.zh2k3ang.jrpc.server.registry;

import com.zh2k3ang.jrpc.common.util.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {

    public static final String REGISTRY_ROOT = "/jrpc";

    @Override
    public boolean registerService(String serviceName, InetSocketAddress address) {

        String path = REGISTRY_ROOT+"/"+serviceName+address.toString();
        CuratorFramework client = CuratorUtil.getClient();
        if(CuratorUtil.checkExists(client, path)) return false;
        CuratorUtil.createEphemeralNode(client, path);
        log.info("service registered: {}, {}", serviceName, address.toString());
        return true;
    }
}
