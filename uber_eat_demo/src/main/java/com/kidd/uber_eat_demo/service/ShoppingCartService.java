package com.kidd.uber_eat_demo.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kidd.uber_eat_demo.model.entity.ShoppingCart;

@Service
public interface ShoppingCartService extends IService<ShoppingCart> {

    // 购物车商品数量新建/加1
    ShoppingCart modifyOne(ShoppingCart shoppingCart, int flag);
}
