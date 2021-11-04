package com.xxl.security.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * Created by macro on 2019/4/8.
 */
@Configuration
@MapperScan({"com.xxl.security.tiny.mbg.mapper","com.xxl.security.tiny.dao"})
public class MyBatisConfig {
}
