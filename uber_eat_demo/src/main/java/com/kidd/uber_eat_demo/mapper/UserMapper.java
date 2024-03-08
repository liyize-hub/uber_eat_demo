package com.kidd.uber_eat_demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kidd.uber_eat_demo.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
