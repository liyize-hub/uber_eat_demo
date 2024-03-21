package com.kidd.uber_eat_demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.model.dto.DishDto;
import com.kidd.uber_eat_demo.model.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    // 在 MyBatis-Plus 中，当你调用 page 方法进行分页查询时，
    // Page 对象中的分页信息会被自动传递到 XML映射文件中。
    // MyBatis-Plus 会根据 Page 对象中的 current 和 size 属性自动生成相应的 SQL 语句，
    // 并将分页信息传递给 SQL 查询语句
    Page<DishDto> pageDishDto(Page<DishDto> page, String name);
}
