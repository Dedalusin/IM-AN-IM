package com.dedalusin.webgate.Balance;

import Constants.ServerConstants;
import entity.ImNode;
import org.apache.curator.framework.CuratorFramework;
import lombok.Data;
import util.JsonUtil;
import zk.CuratorZKclient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ImLoadBalance {
    private CuratorFramework client = null;
    private String managerPath;

    public ImLoadBalance(CuratorZKclient curatorZKclient) {
        this.client = curatorZKclient.getClient();
        managerPath = ServerConstants.MANAGE_PATH;
    }

    public List<ImNode> getWorkers() {
        List<ImNode> workers;
        List<String> childrens = null;
        try {
            childrens = client.getChildren().forPath(ServerConstants.MANAGE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (null == childrens) {
            return null;
        }
        workers = childrens.stream().map(s -> {
            byte[] payload = null;
            try {
                payload = client.getData().forPath(managerPath + "/" + s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ImNode node = JsonUtil.jsonBytes2Object(payload, ImNode.class);
            node.setId(getIdByPath(s));
            return node;
        }).collect(Collectors.toList());
        return workers;
    }

    public Long getIdByPath(String path) {
        if (null == path || !path.contains(ServerConstants.PATH_PREFIX_NO_STRIP)) {
            throw new RuntimeException("path有误");
        }
        return Long.parseLong(path.split(ServerConstants.PATH_PREFIX_NO_STRIP)[1]);
    }
}
