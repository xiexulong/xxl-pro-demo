package com.xxl.jasypt.aop.annotation;

import com.xxl.jasypt.entity.enums.EncryptConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义两个注解@EncryptField、@EncryptMethod分别用在字段属性和方法上，实现思路很简单，
 * 只要方法上应用到@EncryptMethod注解，则检查入参字段是否标注@EncryptField注解，有则将对应字段内容加密。
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptMethod {

    String type() default EncryptConstant.ENCRYPT;
}

