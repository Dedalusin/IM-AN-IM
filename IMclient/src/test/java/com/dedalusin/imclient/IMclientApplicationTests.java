package com.dedalusin.imclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IMclientApplicationTests {

    private Throwable Exception;

    @Test
    void contextLoads() {
    }
    @Test
    void tsetTry() throws Throwable {
        int a = 0;
        try {
            a++;
            throw Exception;

        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(a);
    }

}
