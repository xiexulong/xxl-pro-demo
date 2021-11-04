package com.xxl.cron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling//由于SpringTask已经存在于Spring框架中，所以无需添加依赖。只需要在配置类中添加一个@EnableScheduling注解即可开启SpringTask的定时任务能力。
public class CronApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronApplication.class, args);
    }

}
