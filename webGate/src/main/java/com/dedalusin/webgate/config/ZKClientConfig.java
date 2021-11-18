package com.dedalusin.webgate.config;

import com.dedalusin.webgate.Balance.ImLoadBalance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import util.SpringContextUtil;
import zk.CuratorZKclient;

@Configuration
public class ZKClientConfig implements ApplicationContextAware {

    @Value("${zookeeper.connect.url}")
    private String zkConnect;

    @Value("${zookeeper.connect.SessionTimeout}")
    private String zkSessionTimeout;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.setContext(applicationContext);
    }

    @Bean(name = "curatorzkClient")
    public CuratorZKclient curatorZKclient() {
        return new CuratorZKclient(zkConnect, zkSessionTimeout);
    }

    @Bean(name = "imLoadBalance")
    public ImLoadBalance imLoadBalance(CuratorZKclient curatorZKclient) {
        return new ImLoadBalance(curatorZKclient);
    }

}
