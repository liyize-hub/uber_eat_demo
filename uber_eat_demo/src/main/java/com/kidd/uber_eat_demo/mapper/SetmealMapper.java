package com.kidd.uber_eat_demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.dto.SetmealDto;
import com.kidd.uber_eat_demo.entity.Setmeal;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    Page<SetmealDto> pageDishDto(Page<SetmealDto> SetmealDtoPage, String name);
}
