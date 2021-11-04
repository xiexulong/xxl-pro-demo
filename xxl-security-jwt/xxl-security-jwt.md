
# 整合SpringSecurity和JWT实现认证和授权（一）

> 本文主要讲解通过整合SpringSecurity和JWT实现后台用户的登录和授权功能，同时改造Swagger-UI的配置使其可以自动记住登录令牌进行发送。

## 项目使用框架介绍

### SpringSecurity

> SpringSecurity是一个强大的可高度定制的认证和授权框架，对于Spring应用来说它是一套Web安全标准。SpringSecurity注重于为Java应用提供认证和授权功能，像所有的Spring项目一样，它对自定义需求具有强大的扩展性。

### JWT
> JWT是JSON WEB TOKEN的缩写，它是基于 RFC 7519 标准定义的一种可以安全传输的的JSON对象，由于使用了数字签名，所以是可信任和安全的。

#### JWT的组成

- JWT token的格式：header.payload.signature
- header中用于存放签名的生成算法
```json
{"alg": "HS512"}
```
- payload中用于存放用户名、token的生成时间和过期时间
```json
{"sub":"admin","created":1489079981393,"exp":1489684781}
```
- signature为以header和payload生成的签名，一旦header和payload被篡改，验证将失败
```java
//secret为加密算法的密钥
String signature = HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
```

#### JWT实例
这是一个JWT的字符串
```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImNyZWF0ZWQiOjE1NTY3NzkxMjUzMDksImV4cCI6MTU1NzM4MzkyNX0.d-iki0193X0bBOETf2UN3r3PotNIEAV7mzIxxeI5IxFyzzkOZxS0PGfF_SK6wxCv2K8S0cZjMkv6b5bCqc0VBw
```
可以在该网站上获得解析结果：https://jwt.io/
![](../images/arch_screen_13.png)

#### JWT实现认证和授权的原理

- 用户调用登录接口，登录成功后获取到JWT的token；
- 之后用户每次调用接口都在http的header中添加一个叫Authorization的头，值为JWT的token；
- 后台程序通过对Authorization头中信息的解码及数字签名校验来获取其中的用户信息，从而实现认证和授权。


## 项目使用表说明

- `ums_admin`：后台用户表
- `ums_role`：后台用户角色表
- `ums_permission`：后台用户权限表
- `ums_admin_role_relation`：后台用户和角色关系表，用户与角色是多对多关系
- `ums_role_permission_relation`：后台用户角色和权限关系表，角色与权限是多对多关系
- `ums_admin_permission_relation`：后台用户和权限关系表(除角色中定义的权限以外的加减权限)，加权限是指用户比角色多出的权限，减权限是指用户比角色少的权限

## 整合SpringSecurity及JWT

### 在pom.xml中添加项目依赖
```xml
<!--SpringSecurity依赖配置-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!--Hutool Java工具包-->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>4.5.7</version>
</dependency>
<!--JWT(Json Web Token)登录支持-->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.0</version>
</dependency>
```

### 添加JWT token的工具类

> 用于生成和解析JWT token的工具类

相关方法说明：
- generateToken(UserDetails userDetails) :用于根据登录用户信息生成token
- getUserNameFromToken(String token)：从token中获取登录用户的信息
- validateToken(String token, UserDetails userDetails)：判断token是否还有效

``` sql
create table ums_admin
(
   id                   bigint not null auto_increment,
   username             varchar(64) comment '用户名',
   password             varchar(64) comment '密码',
   icon                 varchar(500) comment '头像',
   email                varchar(100) comment '邮箱',
   nick_name            varchar(200) comment '昵称',
   note                 varchar(500) comment '备注信息',
   create_time          datetime comment '创建时间',
   login_time           datetime comment '最后登录时间',
   status               int(1) default 1 comment '帐号启用状态：0->禁用；1->启用',
   primary key (id)
);

create table ums_role
(
   id                   bigint not null auto_increment,
   name                 varchar(100) comment '名称',
   description          varchar(500) comment '描述',
   admin_count          int comment '后台用户数量',
   create_time          datetime comment '创建时间',
   status               int(1) default 1 comment '启用状态：0->禁用；1->启用',
   sort                 int default 0,
   primary key (id)
);

create table ums_admin_role_relation
(
   id                   bigint not null auto_increment,
   admin_id             bigint,
   role_id              bigint,
   primary key (id)
);
-- 后台菜单表，用于控制后台用户可以访问的菜单，支持隐藏、排序和更改名称、图标。
   
create table ums_menu
(
   id                   bigint not null auto_increment,
   parent_id            bigint comment '父级ID',
   create_time          datetime comment '创建时间',
   title                varchar(100) comment '菜单名称',
   level                int(4) comment '菜单级数',
   sort                 int(4) comment '菜单排序',
   name                 varchar(100) comment '前端名称',
   icon                 varchar(200) comment '前端图标',
   hidden               int(1) comment '前端隐藏',
   primary key (id)
);
-- 后台资源表，用于控制后台用户可以访问的接口，使用了Ant路径的匹配规则，可以使用通配符定义一系列接口的权限。
create table ums_resource
(
   id                   bigint not null auto_increment,
   category_id          bigint comment '资源分类ID',
   create_time          datetime comment '创建时间',
   name                 varchar(200) comment '资源名称',
   url                  varchar(200) comment '资源URL',
   description          varchar(500) comment '描述',
   primary key (id)
);
-- 后台资源分类表，在细粒度进行权限控制时，可能资源会比较多，所以设计了个资源分类的概念，便于给角色分配资源。
create table ums_resource_category
(
   id                   bigint not null auto_increment,
   create_time          datetime comment '创建时间',
   name                 varchar(200) comment '分类名称',
   sort                 int(4) comment '排序',
   primary key (id)
);
-- 后台角色菜单关系表，多对多关系，可以给一个角色分配多个菜单。
create table ums_role_menu_relation
(
   id                   bigint not null auto_increment,
   role_id              bigint comment '角色ID',
   menu_id              bigint comment '菜单ID',
   primary key (id)
);
-- 后台角色资源关系表，多对多关系，可以给一个角色分配多个资源。
create table ums_role_resource_relation
(
   id                   bigint not null auto_increment,
   role_id              bigint comment '角色ID',
   resource_id          bigint comment '资源ID',
   primary key (id)
);

```