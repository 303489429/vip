package com.vip.redis.vip.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * ZK 是没有连接池的概念的 一个连接一个session
 * @author wangzhilong
 * @date 2020/12/14
 */
public class ZkDemo {

    public static void main(String[] args) throws IOException {
        //watch 观察 回调
        //第一类 ，new ZK的时候传入的watch，这个watch是session级别的

        final ZooKeeper zk = new ZooKeeper("zk:2181", 3, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                final Event.KeeperState state = event.getState();
                final Event.EventType type = event.getType();
                final String path = event.getPath();

                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
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
            }
        });

        //第二类，

    }
}
