package com.xxl.mybatis.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.xxl.mybatis.mbg.mapper")
public class MyBatisConfig {
}
