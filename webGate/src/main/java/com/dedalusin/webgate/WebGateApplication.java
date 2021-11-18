package com.dedalusin.webgate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.dedalusin")
public class WebGateApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebGateApplication.class, args);
    }

}
