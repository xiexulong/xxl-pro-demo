package com.xxl.easypoi.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XxlController {

    @ApiOperation(value = "hello")
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {

        return "xxl";
    }
}
