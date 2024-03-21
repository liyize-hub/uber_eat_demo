package com.kidd.uber_eat_demo.model.dto;

import java.util.List;

import com.kidd.uber_eat_demo.model.entity.OrderDetail;
import com.kidd.uber_eat_demo.model.entity.Orders;
import com.kidd.uber_eat_demo.model.entity.Setmeal;
import com.kidd.uber_eat_demo.model.entity.SetmealDish;

import lombok.Data;

@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;
}
