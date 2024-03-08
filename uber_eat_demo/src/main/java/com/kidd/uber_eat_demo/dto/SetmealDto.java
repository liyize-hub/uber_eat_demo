package com.kidd.uber_eat_demo.dto;

import java.util.List;

import com.kidd.uber_eat_demo.entity.Setmeal;
import com.kidd.uber_eat_demo.entity.SetmealDish;

import lombok.Data;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
