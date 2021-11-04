package com.xxl.jasypt.controller;

import com.xxl.jasypt.aop.annotation.EncryptField;
import com.xxl.jasypt.aop.annotation.EncryptMethod;
import com.xxl.jasypt.dynamicquery.DynamicQuery;
import com.xxl.jasypt.entity.dto.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class XxlController {
    @Autowired
    private DynamicQuery dynamicQuery;

    /**
     * http://127.0.0.1:8080/hello
     * @return
     */
    @GetMapping("/hello")
    public List<Object> hello() {
        String nativeSql = "SELECT * FROM ACT_RE_PROCDEF ";
        List<Object> l = dynamicQuery.query(nativeSql);
        System.out.println(l.size());
        return l;
    }


    /**
     curl -H "Content-Type: application/json" -X POST -d '{"age":2,"address":"成都","mobile":"15828015905","userId":2222}' "http://127.0.0.1:8080/test"
     */
    @EncryptMethod
    @PostMapping(value = "test")
    @ResponseBody
    public UserVo testEncrypt(@RequestBody UserVo user) {
        System.out.println("加密后的数据：user" + user.getMobile() + ", address:" + user.getAddress() + ", age" + user.getAge());
        return user;
    }

}
