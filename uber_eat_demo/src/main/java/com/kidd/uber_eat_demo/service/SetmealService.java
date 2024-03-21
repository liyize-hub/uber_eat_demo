package com.kidd.uber_eat_demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kidd.uber_eat_demo.model.dto.DishDto;
import com.kidd.uber_eat_demo.model.dto.SetmealDto;
import com.kidd.uber_eat_demo.model.entity.Setmeal;

@Service
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    // 套餐分页查询，需要向category表中查category的name
    Page<SetmealDto> pageDishDto(Page<SetmealDto> SetmealDtoPage, String name);

    // 根据id查询套餐信息和对应的菜品信息
    SetmealDto getByIdWithDishes(Long id);

    // 更新菜品信息，同时更新对应的口味信息
    void updateWithDishes(SetmealDto setmealDto);

    // 删除套餐，需要批量删除，需要删除dish_flavor中的相关字段
    boolean removeByIdsWithDishes(List<Long> ids);
}
