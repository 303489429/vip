package com.vip.redis.vip.zookeeper.config;

import com.vip.redis.vip.zookeeper.util.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wangzhilong
 * @date 2020/12/16
 */
public class TestConfig {

    private ZooKeeper zooKeeper ;

    @Before
    public void before(){
        zooKeeper = ZKUtils.getZookeeperInstance("/testConfig");
    }

    @After
    public void after(){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConfig() {
        MyConf conf = new MyConf();
        GetConfigWatcher watcher = new GetConfigWatcher();
        watcher.setZooKeeper(zooKeeper);
        watcher.setConf(conf);
        watcher.await();

        while (true) {
            if ("".equals(conf.getConf())) {
                watcher.await();
            }
            System.out.println(conf.getConf());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
