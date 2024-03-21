package com.kidd.uber_eat_demo.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.kidd.uber_eat_demo.model.entity.Dish;
import com.kidd.uber_eat_demo.model.entity.DishFlavor;

import lombok.Data;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
