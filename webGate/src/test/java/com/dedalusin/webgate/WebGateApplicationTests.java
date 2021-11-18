package com.dedalusin.webgate;

import Constants.ServerConstants;
import com.dedalusin.webgate.Balance.ImLoadBalance;
import entity.ImNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import zk.CuratorZKclient;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
@EntityScan(basePackages = "com.dedalusin")
class WebGateApplicationTests {
    @Resource
    ImLoadBalance imLoadBalance;
    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        CuratorZKclient curatorZKclient = CuratorZKclient.getSingleton();
        //curatorZKclient.createNode(ServerConstants.MANAGE_PATH, "Im parent");
        curatorZKclient.createEphemeralSeqNode(ServerConstants.PATH_PREFIX);
        curatorZKclient.createEphemeralSeqNode(ServerConstants.PATH_PREFIX);
        curatorZKclient.createEphemeralSeqNode(ServerConstants.PATH_PREFIX);
        List<ImNode> list = imLoadBalance.getWorkers();
        System.out.println(list);
    }

}
