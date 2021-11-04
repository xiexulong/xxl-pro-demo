package com.xxl.rabbitmq.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
public interface OmsPortalOrderService {

    @Transactional
    public void generateOrder();
    /**
     * 取消单个超时订单
     */
    @Transactional
    void cancelOrder(Long orderId);
}
