package com.kidd.uber_eat_demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kidd.uber_eat_demo.dto.DishDto;
import com.kidd.uber_eat_demo.entity.Dish;

@Service
public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    // 更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    // 菜品信息分页查询,需要向category表中查category的name
    Page<DishDto> pageDishDto(Page<DishDto> dishdtoPage, String name);

    // 删除菜品，需要批量删除，需要删除dish_flavor中的相关字段
    boolean removeByIdsWithFlavors(List<Long> ids);

    List<DishDto> getFullList(Dish dish);

}
