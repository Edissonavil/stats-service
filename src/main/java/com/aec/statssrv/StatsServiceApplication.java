// src/main/java/com/aec/statssrv/StatsServiceApplication.java
package com.aec.statssrv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.aec.statssrv.client")
public class StatsServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(StatsServiceApplication.class, args);
  }
}
