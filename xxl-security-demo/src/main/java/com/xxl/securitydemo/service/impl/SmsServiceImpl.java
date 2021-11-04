package com.xxl.securitydemo.service.impl;

import com.xxl.securitydemo.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void send() {
        System.out.println("短信发送成功！");
    }
}
