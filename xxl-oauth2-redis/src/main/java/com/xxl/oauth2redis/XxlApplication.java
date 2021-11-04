package com.xxl.oauth2redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * xxl-oauth2项目中token是存在内存中的，如果部署多个服务，就会导致无法使用令牌的问题。
 * Spring Cloud Security中有两种存储令牌的方式可用于解决该问题，一种是使用Redis来存储，另一种是使用JWT来存储。
 * 本项目演示使用redis存储token令牌
 */
@SpringBootApplication
public class XxlApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlApplication.class, args);
    }

}
