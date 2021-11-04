package com.xxl.securityjwt2.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.xxl.securityjwt2.domain.PayloadDto;

import java.text.ParseException;

/**
 *  对称加密（HMAC）:对称加密指的是使用相同的秘钥来进行加密和解密，
 *  如果你的秘钥不想暴露给解密方，考虑使用非对称加密。
 *  非对称加密（RSA）:非对称加密指的是使用公钥和私钥来进行加密解密操作。对于加密操作，公钥负责加密，私钥负责解密，对于签名操作，私钥负责签名，公钥负责验证。非对称加密在JWT中的使用显然属于签名操作。
 */
public interface JwtTokenService {

    /**
     * 使用HMAC算法生成token
     */
    String generateTokenByHMAC(String payloadStr, String secret) throws JOSEException;

    /**
     * 使用HMAC算法校验token
     */
    PayloadDto verifyTokenByHMAC(String token, String secret) throws ParseException, JOSEException;

    /**
     * 使用RSA算法生成token
     */
    String generateTokenByRSA(String payloadStr, RSAKey rsaKey) throws JOSEException;

    /**
     * 使用RSA算法校验token
     */
    PayloadDto verifyTokenByRSA(String token, RSAKey rsaKey) throws ParseException, JOSEException;

    /**
     * 获取默认payload
     */
    PayloadDto getDefaultPayloadDto();

    /**
     * 获取默认的RSAKey
     */
    RSAKey getDefaultRSAKey();
}
