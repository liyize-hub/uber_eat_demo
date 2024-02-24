package com.kidd.uber_eat_demo.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kidd.uber_eat_demo.entity.Category;

@Service
public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
