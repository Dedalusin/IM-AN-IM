package com.dedalusin.imserver.distributed;

import Constants.ServerConstants;
import entity.ImNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZKUtil;
import util.JsonUtil;
import zk.CuratorZKclient;

/**
 * @author Administrator
 */
@Data
@Slf4j
public class ImWorker {

    private CuratorFramework client = null;
    //Znode路径，创建后返回
    private String pathRegistered = null;

    private ImNode localNode = null;

    private static ImWorker singleInstance = null;

    public static ImWorker getInst() {
        if (null == singleInstance) {
            synchronized (ImWorker.class) {
                if (null == singleInstance) {
                    singleInstance = new ImWorker();
                    singleInstance.localNode = new ImNode();
                }
            }
        }
        return singleInstance;
    }

    private ImWorker() {
    }

    public void init() {
        if (null == client) {
            synchronized (CuratorFramework.class) {
                if (null == client) {
                    doInit();
                }
            }
        }
    }

    private void doInit() {
        this.client = CuratorZKclient.getSingleton().getClient();
        //创建ZNode
        try {
            byte[] payload = JsonUtil.object2JsonBytes(localNode);
            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ServerConstants.PATH_PREFIX, payload);
            //设置分布式节点id
            localNode.setId(CuratorZKclient.getIdByPath(pathRegistered));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLocalNode(String ip, int port) {
        localNode.setHost(ip);
        localNode.setPort(port);
    }

    /**
     * 增加负载，表示有用户登录成功
     *
     * @return 成功状态
     */
    public boolean incBalance() {
        if (null == localNode) {
            throw new RuntimeException("还没有设置Node 节点");
        }
        // 增加负载：增加负载，并写回zookeeper
        while (true) {
            try {
                localNode.incrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localNode);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

    /**
     * 减少负载，表示有用户下线，写回zookeeper
     *
     * @return 成功状态
     */
    public boolean decrBalance() {
        if (null == localNode) {
            throw new RuntimeException("还没有设置Node 节点");
        }
        while (true) {
            try {
                localNode.decrementBalance();
                byte[] payload = JsonUtil.object2JsonBytes(localNode);
                client.setData().forPath(pathRegistered, payload);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}