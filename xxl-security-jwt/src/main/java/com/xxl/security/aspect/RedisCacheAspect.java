package com.xxl.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redis缓存切面，主要需要防止Redis宕机影响正常业务逻辑
 * 对于影响性能的，频繁查询数据库的操作，我们可以通过Redis作为缓存来优化。
 * 缓存操作不该影响正常业务逻辑，我们可以使用AOP来统一处理缓存操作中的异常。
 */
@Aspect
@Component
@Order(2)
public class RedisCacheAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(RedisCacheAspect.class);

    /**
     * execution(public * com.xxl.security.portal.service.*CacheService.*(..)) ||
     * execution(public * com.xxl.security.service.*CacheService.*(..))
     */
    @Pointcut("execution(public * com.xxl.security.service.*CacheService.*(..))")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            /**
             * 不过并不是所有的方法都需要处理异常的，比如我们的验证码存储，如果我们的Redis宕机了，我们的验证码存储接口需要的是报错，而不是返回执行成功。
             * 对于上面这种需求我们可以通过自定义注解来完成，首先我们自定义一个CacheException注解，如果方法上面有这个注解，发生异常则直接抛出。
             */
            //有CacheException注解的方法需要抛出异常
            if (method.isAnnotationPresent(CacheException.class)) {
                throw throwable;
            } else {
                LOGGER.error(throwable.getMessage());
            }
        }
        return result;
    }

}
