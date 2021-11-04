package com.xxl.sso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 在接口上配置权限时使用
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 使用静态权限配置
@Order(101)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
}
