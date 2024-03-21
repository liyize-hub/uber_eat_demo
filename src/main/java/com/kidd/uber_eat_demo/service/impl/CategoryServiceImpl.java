package com.kidd.uber_eat_demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.mapper.CategoryMapper;
import com.kidd.uber_eat_demo.model.entity.Category;
import com.kidd.uber_eat_demo.model.entity.Dish;
import com.kidd.uber_eat_demo.model.entity.Setmeal;
import com.kidd.uber_eat_demo.service.CategoryService;
import com.kidd.uber_eat_demo.service.DishService;
import com.kidd.uber_eat_demo.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        // 1. 查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> DishqueryWrapper = Wrappers.lambdaQuery(Dish.class);
        DishqueryWrapper.eq(Dish::getCategoryId, id);
        if (dishService.count(DishqueryWrapper) > 0) {
            // 已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        // 2. 查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> SetmealqueryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        SetmealqueryWrapper.eq(Setmeal::getCategoryId, id);
        if (setmealService.count(SetmealqueryWrapper) > 0) {
            // 已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 3. 正常删除分类
        super.removeById(id);
    }

}
