package com.dedalusin.imserver.distributed;

import Constants.ServerConstants;
import entity.ImNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

@Data
@Slf4j
public class WorkerRouter {
    private CuratorFramework client = null;
    private String pathRegistered = null;
    private ImNode node = null;

    private static WorkerRouter singleInstance = null;
    private static final String parentPath = ServerConstants.MANAGE_PATH;


}
