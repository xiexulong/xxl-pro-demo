
该项目模拟两种模式：授权码模式和密码登陆两种模式


授权码的模式这种方式是将token放到内存里面，如果部署多个服务，就会导致无法使用令牌的问题,
 * 有两种存储令牌的方式可用于解决该问题，一种是使用Redis来存储，另一种是使用JWT来存储。

没登陆的话会自动跳转到登陆界面：http://localhost:8088/xxl/login  

http://localhost:8088/xxl/oauth/authorize?response_type=code&client_id=admin&redirect_uri=http://www.baidu.com&scope=all&state=normal