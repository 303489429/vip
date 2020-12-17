package com.vip.redis.vip.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author wangzhilong
 * @date 2020/12/16
 */
public class GetConfigWatcher implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {


    private ZooKeeper zooKeeper;

    private MyConf conf;

    private CountDownLatch cc = new CountDownLatch(1);

    public void setConf(MyConf conf) {
        this.conf = conf;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void await(){
        try {
            //获取一个配置，并watch他
            zooKeeper.exists("/AppConfig", this, this, "exists");
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * watcher 实现方法
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        //监控数据变化
        final Event.EventType type = watchedEvent.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                zooKeeper.getData("/AppConfig", this, this, "NodeCreated");
                break;
            case NodeDeleted:
                conf.setConf("");
                System.out.println("数丢了....");
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                //数据变化
                zooKeeper.getData("/AppConfig", this, this, "NodeDataChanged");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }

    /**
     * exists 方法的回调 AsyncCallback.StatCallback method
     * stat 不为空说明数据存在
     *
     * @param i
     * @param s
     * @param o
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat != null) {
            //说明数据存在
            zooKeeper.getData("/AppConfig", this, this, "getData");
        }
    }

    /**
     * getData 方法的回调实现 DataCallback method
     *
     * @param i
     * @param s
     * @param o
     * @param bytes
     * @param stat
     */
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        //设置获取的值
        conf.setConf(new String(bytes));
        //获取到数据后减1
        cc.countDown();
    }
}
