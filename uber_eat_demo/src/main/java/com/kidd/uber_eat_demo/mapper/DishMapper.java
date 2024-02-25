package com.kidd.uber_eat_demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.dto.DishDto;
import com.kidd.uber_eat_demo.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    Page<DishDto> pageDishDto(Page<DishDto> page, String name);
}
