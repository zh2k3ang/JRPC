package com.zh2k3ang.jrpc.registry.zk.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CuratorUtil {

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static CuratorFramework client;

    private CuratorUtil(){}

    public static CuratorFramework getClient() {
        RetryPolicy policy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        client = CuratorFrameworkFactory.builder()
                .connectString("49.235.55.170:2181")
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
