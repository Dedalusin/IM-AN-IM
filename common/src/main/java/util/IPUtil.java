package util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public class IPUtil {
    public static String getHostAddress() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error("获取ip失败");
        }
        return ip;
    }
}
