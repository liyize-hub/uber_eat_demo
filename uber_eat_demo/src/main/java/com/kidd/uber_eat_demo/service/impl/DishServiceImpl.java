package com.kidd.uber_eat_demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.entity.Dish;
import com.kidd.uber_eat_demo.mapper.DishMapper;
import com.kidd.uber_eat_demo.service.DishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}
