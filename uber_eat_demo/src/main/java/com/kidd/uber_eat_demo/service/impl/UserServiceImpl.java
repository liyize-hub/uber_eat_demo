package com.kidd.uber_eat_demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.mapper.UserMapper;
import com.kidd.uber_eat_demo.model.entity.User;
import com.kidd.uber_eat_demo.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}