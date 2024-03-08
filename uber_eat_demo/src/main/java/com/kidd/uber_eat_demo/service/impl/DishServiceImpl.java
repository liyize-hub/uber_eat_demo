package com.kidd.uber_eat_demo.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.dto.DishDto;
import com.kidd.uber_eat_demo.entity.Category;
import com.kidd.uber_eat_demo.entity.Dish;
import com.kidd.uber_eat_demo.entity.DishFlavor;
import com.kidd.uber_eat_demo.entity.SetmealDish;
import com.kidd.uber_eat_demo.mapper.DishMapper;
import com.kidd.uber_eat_demo.service.CategoryService;
import com.kidd.uber_eat_demo.service.DishFlavorService;
import com.kidd.uber_eat_demo.service.DishService;
import com.kidd.uber_eat_demo.service.SetmealDishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

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
    @Transactional
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
    @Transactional
    public boolean removeByIdsWithFlavors(List<Long> ids) {
        // 先判断是否在售，如果在售不能删除
        List<Dish> list = this.listByIds(ids);
        for (Dish dish : list) {
            if (dish.getStatus() == 1) {
                throw new CustomException("此菜品正在热卖中，，不能删除");
            }
        }

        // 再次判断是否在套餐中，是否是，则删除不了，如果不在可以删除？
        LambdaQueryWrapper<SetmealDish> setmealDishqw = new LambdaQueryWrapper<>();
        setmealDishqw.in(SetmealDish::getDishId, ids);
        int count = setmealDishService.count(setmealDishqw);
        if (count > 0) {
            throw new CustomException("此菜品在套餐中，不能删除;");
        }

        // 先删除菜品基本信息
        boolean flag1 = this.removeByIds(ids);

        // 再删除关系表中的数据 -- dish_flavor
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = Wrappers.lambdaQuery();
        dishFlavorQueryWrapper.in(DishFlavor::getDishId, ids);
        boolean flag2 = dishFlavorService.remove(dishFlavorQueryWrapper);

        return flag1 && flag2;
    }

    @Override
    @Transactional
    public List<DishDto> getFullList(Dish dish) {
        Long categoryId = dish.getCategoryId();
        List<DishDto> dishDtoList = null;

        // 1. 先从redis中获取缓存数据
        // 动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();// dish_1397844391040167938_1
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null) {
            // 如果存在，直接返回，无需查询数据库
            return dishDtoList;
        }

        // 2. 如果不存在，查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = Wrappers.lambdaQuery(Dish.class);
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = this.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = Wrappers.lambdaQuery(DishFlavor.class);
            flavorQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
            // SQL:select * from dish_flavor where dish_id = ?
            dishDto.setFlavors(dishFlavorService.list(flavorQueryWrapper));

            return dishDto;

        }).collect(Collectors.toList());

        // 如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis,1个小时的超时时间
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return dishDtoList;

    }

}
