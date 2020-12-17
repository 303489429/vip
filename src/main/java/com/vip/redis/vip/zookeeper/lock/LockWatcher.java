package com.vip.redis.vip.zookeeper.lock;

import com.vip.redis.vip.zookeeper.util.ZKUtils;
import org.apache.zookeeper.AddWatchMode;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author wangzhilong
 * @date 2020/12/16
 */
public class LockWatcher implements Watcher , AsyncCallback.StringCallback, AsyncCallback.Children2Callback , AsyncCallback.StatCallback {


    private ZooKeeper zooKeeper ;

    private CountDownLatch cc = new CountDownLatch(1);

    private boolean isRetry = false;

    /**
     * 本次创建的临时节点
     */
    private String curPath ;
    /**
     * 加锁节点名称
     */
    private String threadName ;

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    /**
     * 通过创建一个节点成功，并且是第一个表示加锁成功
     * 加锁
     */
    public void tryLock(){
        try {
            //重入
            byte[] data = zooKeeper.getData("/", false, new Stat());
            if (data.length > 0) {
                String curThread = new String(data);
                if (threadName.equals(curThread)) {
                    isRetry = true;
                    System.out.println("锁重入成功");
                    return;
                }
            }
            //创建节点
            zooKeeper.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this,threadName);
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放锁
     */
    public void unLock(){
        try {
            if (isRetry) {
                //清空数据
                zooKeeper.setData("/", "".getBytes(),-1);
                isRetry = false;
                return;
            }
            //删除节点
            zooKeeper.delete(curPath,-1);
        }  catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * watcher 监听节点状态事件
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        final Event.EventType type = watchedEvent.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                //获取子节点判断是否是第一个 是则进行处理
                zooKeeper.getChildren("/",false,this,"NodeDeleted getChildren");
                break;
            case NodeDataChanged:
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
     * create时的回调 String call back
     * @param rc
     * @param path
     * @param ctx
     * @param name
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //创建成功是name 不为空的情况下
        if (name != null) {
            //name示例 /lock0000000070
            curPath = name;
            System.out.println(threadName +" create call back -->" + path +",name=" + name);
            //创建成功后获取当前节点下的所有子节点，判断是否是最小的那个是则获取锁，不是则监控它前面的节点，前面节点删除后会通知当前节点
            zooKeeper.getChildren("/",false,this,ctx);
        }

    }

    /**
     * getChildren call back method
     *
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
//        System.out.println(threadName + ",path="+path+" ----- 获取的子节点");
//        if (stat != null) {
//            for (String child : children) {
//                System.out.println(child);
//            }
//        }
        //children子节点是无序的 ，并且是lock0000000077 没有斜杠的
        if (CollectionUtils.isEmpty(children)) {
            System.out.println(ctx + ",children list null");
            return;
        }
        Collections.sort(children);
        int index = children.indexOf(curPath.substring(1));
        if (index < 1) {
            //说明当前节点是第一个节点，可以获取锁
            //获取锁时，将当前已经获取或的线程名称设置为当前父节点的值，用于重入锁使用
            try {
                zooKeeper.setData("/", threadName.getBytes(), -1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cc.countDown();
            }
            // 执行
        } else {
            // 监控他前一个节点
            zooKeeper.exists("/"+children.get(index-1),this,this,"exists");
        }
    }

    public String getCurPath() {
        return curPath;
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //
    }
}
