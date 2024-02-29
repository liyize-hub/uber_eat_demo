package com.kidd.uber_eat_demo.dto;

import java.util.List;

import com.kidd.uber_eat_demo.entity.OrderDetail;
import com.kidd.uber_eat_demo.entity.Orders;
import com.kidd.uber_eat_demo.entity.Setmeal;
import com.kidd.uber_eat_demo.entity.SetmealDish;

import lombok.Data;

@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;
}
