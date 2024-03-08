package com.kidd.uber_eat_demo.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kidd.uber_eat_demo.entity.Orders;

@Service
public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
