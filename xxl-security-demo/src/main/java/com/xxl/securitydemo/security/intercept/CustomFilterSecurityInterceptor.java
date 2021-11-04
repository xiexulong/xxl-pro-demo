package com.xxl.securitydemo.security.intercept;

import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * 自定义 FilterSecurityInterceptor。注意，该类没有任何实质性内容，空类，但不可省略
 *
 * FilterSecurityInterceptor 作为 Spring Security Filter Chain 的最后一个 Filter，承担着非常重要的作用。
 *          如获取当前 request 对应的权限配置，调用访问控制器进行鉴权操作等，都是核心功能
 */
public class CustomFilterSecurityInterceptor extends FilterSecurityInterceptor {
}
