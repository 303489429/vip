package com.vip.redis.vip.zookeeper.lock;

import com.vip.redis.vip.zookeeper.util.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wangzhilong
 * @date 2020/12/16
 */
public class TestLock {

    private ZooKeeper zooKeeper ;

    @Before
    public void before(){
        zooKeeper = ZKUtils.getZookeeperInstance("/testLock");
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
    public void testLock(){

        //定义10个线程进行并发获取锁
        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    //获取锁
                    String threadName = Thread.currentThread().getName();
                    LockWatcher lockWatcher = new LockWatcher();
                    lockWatcher.setZooKeeper(zooKeeper);
                    lockWatcher.setThreadName(threadName);
                    lockWatcher.tryLock();
                    //TODO 干活太快可能会有问题，其它线程的锁还没有创建或者创建了，但是还没来得及监控他前一个节点的任务，
                    // 前一个节点已经干完活触发事件删除了
                    System.out.println(threadName + "---干活啦..., 我的序号-->" + lockWatcher.getCurPath());
                    if (Math.random() > 0.7) {
                        lockWatcher.tryLock();
                        System.out.println(threadName + "---重入锁又干活啦..., 我的序号-->" + lockWatcher.getCurPath());
                        lockWatcher.unLock();
                    }
                    //干活
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //释放锁
                    lockWatcher.unLock();

                }
            }.start();
        }

        while (true) {

        }

    }

}
