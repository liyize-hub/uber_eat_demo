package com.kidd.uber_eat_demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.mapper.SetmealMapper;
import com.kidd.uber_eat_demo.model.dto.DishDto;
import com.kidd.uber_eat_demo.model.dto.SetmealDto;
import com.kidd.uber_eat_demo.model.entity.Dish;
import com.kidd.uber_eat_demo.model.entity.DishFlavor;
import com.kidd.uber_eat_demo.model.entity.Setmeal;
import com.kidd.uber_eat_demo.model.entity.SetmealDish;
import com.kidd.uber_eat_demo.service.SetmealDishService;
import com.kidd.uber_eat_demo.service.SetmealService;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * 
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        // 向SetmealDish 中批量添加Setmeal_id;
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public Page<SetmealDto> pageDishDto(Page<SetmealDto> SetmealDtoPage, String name) {
        return this.getBaseMapper().pageDishDto(SetmealDtoPage, name);
    }

    /**
     * 根据id查询套餐信息和对应的菜品信息
     * 
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDishes(Long id) {
        // 查询套餐基本信息，从setmeal表查询
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询当前菜品对应的口味信息，从setmeal_dish表查询

        LambdaQueryWrapper<SetmealDish> queryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishs = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishs);

        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        // 更新setmeal表基本信息
        this.updateById(setmealDto);

        Long setmealId = setmealDto.getId();
        // 清理当前套餐对应菜品数据---setmeal_dish表的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);

        // 添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);

    }

    @Override
    @Transactional
    public boolean removeByIdsWithDishes(List<Long> ids) {
        // 先判断是否在售，如果在售不能删除
        List<Setmeal> list = this.listByIds(ids);
        for (Setmeal setmeal : list) {
            if (setmeal.getStatus() == 1) {
                throw new CustomException("此套餐正在热卖中，，不能删除");
            }
        }
        // 先删除套餐基本信息
        boolean flag1 = this.removeByIds(ids);

        // 再删除关系表中的数据 -- setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper = Wrappers.lambdaQuery(SetmealDish.class);
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        boolean flag2 = setmealDishService.remove(queryWrapper);

        return flag1 && flag2;
    }

}
