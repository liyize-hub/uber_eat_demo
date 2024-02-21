package com.kidd.uber_eat_demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kidd.uber_eat_demo.entity.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
