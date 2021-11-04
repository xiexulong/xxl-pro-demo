@MapperScan  
背景：在这之前可以使用@Mapper来注解实体类对应的接口，这样接口就会自动生成对应的实现类，如果想要每个接口都要变成实现类，那么需要在每个接口类上加上@Mapper注解，比较麻烦，解决这个问题用@MapperScan。  

作用：指定要变成实现类的接口所在的包，然后包下面的所有接口在编译之后都会生成相应的实现类
添加位置：可以在在Springboot启动类上面添加，也可以单独创建配置文件使用。  

使用方式：
@MapperScan("com.xxl.mybatis.mbg.mapper")
@MapperScan("com.xxl.mybatis.mbg.bus.mapper","com.xxl.mybatis.mbg.sys.mapper")  
@MapperScan("com.xxl.mybatis.mbg.*.mapper","com.xxl.common.mbg.*.mapper")  

 