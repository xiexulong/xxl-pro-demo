# 1、springboot项目数据库密码如何加密  
## 1.1、方案一、使用druid数据库连接池对数据库密码加密  

  
  
注：druid只能对数据库密码加密  


## 1.2、方案二：使用jasypt对数据库密码加密


在生产环境中，建议使用如下方式配置密钥，避免密钥泄露
```
java -jar -Djasypt.encryptor.password=lybgeek
```


注：jasypt这种方案，因为它不仅可以对密码加密，也可以对其他内容加密。



# 2、AOP字段属性脱敏


curl -H "Content-Type: application/json" -X POST -d '{"age":2,"address":"成都","mobile":"15828015905","userId":2222}' "http://127.0.0.1:8080/test"