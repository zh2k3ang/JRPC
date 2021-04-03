package com.zh2k3ang.jrpc.common.util;

import com.zh2k3ang.jrpc.common.enums.RpcErrorMessageEnum;
import com.zh2k3ang.jrpc.common.enums.RpcPropertyEnum;
import com.zh2k3ang.jrpc.common.exceptions.RpcException;
import com.zh2k3ang.jrpc.common.util.PropertyUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class CuratorUtil {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework client;

    private CuratorUtil(){}

    public static CuratorFramework getClient() {

        Properties properties = PropertyUtil.readPropertiesFile(RpcPropertyEnum.PRC_CONFIG_PATH.getPropertyValue());
        if(properties == null || properties.getProperty(RpcPropertyEnum.ZK_ADDRESS.getPropertyValue())==null)
            throw new RpcException(RpcErrorMessageEnum.SERVICE_NOT_FOUND, "can not find zookeeper address");

        String address = properties.getProperty(RpcPropertyEnum.ZK_ADDRESS.getPropertyValue());
        RetryPolicy policy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(policy)
                .build();
        client.start();

        try {
            client.blockUntilConnected(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client;
    }

    public static void createPersistentNode(CuratorFramework client, String path) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createEphemeralNode(CuratorFramework client, String path) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getChildrenNodes(CuratorFramework client, String path) {
        List<String> res = null;
        try {
            res = client.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean checkExists(CuratorFramework client, String path) {
        Stat stat = null;
        try {
            stat = client.checkExists().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat != null;
    }
}
