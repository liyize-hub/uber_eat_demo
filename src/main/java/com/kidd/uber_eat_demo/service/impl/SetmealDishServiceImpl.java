package com.kidd.uber_eat_demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.mapper.SetmealDishMapper;
import com.kidd.uber_eat_demo.model.entity.SetmealDish;
import com.kidd.uber_eat_demo.service.SetmealDishService;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
