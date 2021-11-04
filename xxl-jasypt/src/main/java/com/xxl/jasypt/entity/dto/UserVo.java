package com.xxl.jasypt.entity.dto;

import com.xxl.jasypt.aop.annotation.EncryptField;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private Long userId;

    @EncryptField
    private String mobile;

    @EncryptField
    private String address;

    private String age;

}

