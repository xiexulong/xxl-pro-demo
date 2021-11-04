package com.xxl.prometheus.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class XxlController {
    private AtomicLong xxlGauge;
    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    public void init() {
        Tags tags = Tags.of("xxl_name", "xxl_type");
        xxlGauge = registry.gauge("xxl",tags, new AtomicLong(0));
    }

    /**
     * http://127.0.0.1:8080/hello
     * http://localhost:8080/actuator/prometheus
     * @return
     */
    @GetMapping("/hello")
    public String hello(Integer count) {
        xxlGauge.set(count);
        return "hello xxl";
    }



}
