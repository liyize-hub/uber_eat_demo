package com.kidd.uber_eat_demo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.dto.DishDto;
import com.kidd.uber_eat_demo.entity.Dish;
import com.kidd.uber_eat_demo.entity.DishFlavor;
import com.kidd.uber_eat_demo.mapper.DishMapper;
import com.kidd.uber_eat_demo.service.DishFlavorService;
import com.kidd.uber_eat_demo.service.DishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增菜品，同时保存对应的口味数据
     * 
     * @Transactional也可以写在接口的方法上
     * @param dishDto
     */
    // 涉及到多张表的操作，需要加入事务控制
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();// 菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 方式1：使用遍历循环给口味表设置dishid
        // for (DishFlavor flavor : flavors) {
        // flavor.setDishId(dishDto.getId());
        // }
        // 方式2：使用stream流
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());// 转换为list形式

        // 保存菜品口味数据到菜品口味表dish_flavor
        // saveBatch批量插入数据到数据库
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * 
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);
        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper); // list 根据查询条件返回多个值，构成一个list
        dishDto.setFlavors(flavors);

        return dishDto;

    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        Long dishId = dishDto.getId();

        // 清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);

        // 添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public Page<DishDto> pageDishDto(Page<DishDto> dishDtoPage, String name) {

        // MP 导入
        // return this.getBaseMapper().pageDishDto(dishDtoPage, name);

        // 原始
        return dishMapper.pageDishDto(dishDtoPage, name);
    }

    @Override
    public boolean removeByIdsWithFlavors(List<Long> ids) {
        // 先判断是否在售，如果在售不能删除
        List<Dish> list = this.listByIds(ids);
        for (Dish dish : list) {
            if (dish.getStatus() == 1) {
                throw new CustomException("此菜品正在热卖中，，不能删除");
            }
        }

        // // 再次判断是否在套餐中，是否是，则删除不了，如果不在可以删除？
        // LambdaQueryWrapper<SetmealDish> smdlqw = new LambdaQueryWrapper<>();
        // smdlqw.in(SetmealDish::getDishId, ids);
        // int count = setmealDishService.count(smdlqw);
        // if (count > 0) {
        // throw new CustomException("此菜品在套餐中，不能删除;");
        // }

        // 先删除菜品基本信息
        boolean flag1 = this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = Wrappers.lambdaQuery();
        dishFlavorQueryWrapper.in(DishFlavor::getDishId, ids);
        boolean flag2 = dishFlavorService.remove(dishFlavorQueryWrapper);

        return flag1 && flag2;
    }

}
