package com.kidd.uber_eat_demo.model.dto;

import java.util.List;

import com.kidd.uber_eat_demo.model.entity.Setmeal;
import com.kidd.uber_eat_demo.model.entity.SetmealDish;

import lombok.Data;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
