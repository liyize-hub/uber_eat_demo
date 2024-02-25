package com.kidd.uber_eat_demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.kidd.uber_eat_demo.entity.Dish;
import com.kidd.uber_eat_demo.entity.DishFlavor;

import lombok.Data;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
