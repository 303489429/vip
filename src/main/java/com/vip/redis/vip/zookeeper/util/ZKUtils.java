package com.vip.redis.vip.zookeeper.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author wangzhilong
 * @date 2020/12/16
 */
public class ZKUtils {

    private static String address = "172.22.33.243:2181,172.22.33.244:2181,172.22.33.234:2181";

    private static ZooKeeper zooKeeper ;

    public static ZooKeeper getZookeeperInstance(String parentPath){
        CountDownLatch cc = new CountDownLatch(1);
        try {
            zooKeeper = new ZooKeeper(address+parentPath, 2000, watchedEvent -> {
                switch (watchedEvent.getState()) {
                    case Unknown:
                        break;
                    case Disconnected:
                        System.out.println("默认监控的连接断开");
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("default watch connected....");
                        cc.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }
            });
            cc.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }
}
