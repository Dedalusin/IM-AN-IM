package zk;

import Constants.ServerConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import util.SpringContextUtil;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public class CuratorZKclient {
    private final String zkSessionTimeout;
    private CuratorFramework client;

    //配置默认项
    private String zkAddress = "127.0.0.1:2181";
    public static CuratorZKclient instance = null;

    private static CuratorZKclient singleton = null;

    public static CuratorZKclient getSingleton() {
        if (null == singleton) {
            //bean 生成不在common模块
            singleton = SpringContextUtil.getBean("curatorzkClient");
        }
        return singleton;
    }

    public CuratorZKclient(String zkAddress, String zkSessionTimeout) {
        this.zkAddress = zkAddress;
        this.zkSessionTimeout = zkSessionTimeout;
        init();
    }

    public void init() {
        if (null != client) {
            return;
        }
        client = ClientFactory.createSimple(zkAddress, zkSessionTimeout);
        client.start();
        instance = this;
    }

    public void destroy() {
        CloseableUtils.closeQuietly(client);
    }

    /**
     * @param zkPath
     * @param data   创建持久节点
     */
    public void createNode(String zkPath, String data) {
        try {
            byte[] payload = "to set content".getBytes(StandardCharsets.UTF_8);
            if (null != data) {
                payload = data.getBytes(StandardCharsets.UTF_8);
            }
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNode(String zkPath) {
        try {
            if (!isNodeExist(zkPath)) {
                return;
            }
            client.delete().forPath(zkPath);
            log.info("已删除节点" + zkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNodeExist(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            if (null == stat) {
                log.info(path + "节点不存在");
                return false;
            } else {
                log.info(path + "节点存在");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String createEphemeralSeqNode(String zkPath) {
        try {
            return client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(zkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getIdByPath(String path) {
        if (null == path || !path.contains(ServerConstants.PATH_PREFIX_NO_STRIP)) {
            throw new RuntimeException("path有误");
        }
        return Long.parseLong(path.split(ServerConstants.PATH_PREFIX_NO_STRIP)[1]);
    }

}
