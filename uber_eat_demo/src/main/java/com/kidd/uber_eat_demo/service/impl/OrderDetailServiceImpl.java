package com.kidd.uber_eat_demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.mapper.OrderDetailMapper;
import com.kidd.uber_eat_demo.model.entity.OrderDetail;
import com.kidd.uber_eat_demo.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
