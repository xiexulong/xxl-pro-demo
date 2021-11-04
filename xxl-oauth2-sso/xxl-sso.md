# 目的
Spring Cloud Security 构建安全结合Oauth2可以实现单点登录功能，本项目模拟单点登录。  

# 什么是SSO
单点登录（Single Sign On）指的是当有多个系统需要登录时，用户只需登录一个系统，就可以访问其他需要登录的系统而无需登录。  

这里我们创建一个xxl-oauth2-sso服务作为需要登录的客户端服务，使用上一节中的xxl-oauth2-jwt服务作为认证服务，  
当我们在xxl-oauth2-jwt服务上登录以后，就可以直接访问xxl-oauth2-sso需要登录的接口



演示步骤  
启动xxl-oauth2-sso客户端服务和xxl-oauth2-jwt认证服务；  

访问客户端需要授权的接口http://localhost:8081/xxl/user/getCurrentUser会跳转到授权服务的登录界面；  
进行登录操作后跳转到授权页面（我们这里设置的是自动授权）  
授权后会跳转到原来需要权限的接口地址，展示登录用户信息；  


api操作参考：http://www.macrozheng.com/#/cloud/oauth2_sso

