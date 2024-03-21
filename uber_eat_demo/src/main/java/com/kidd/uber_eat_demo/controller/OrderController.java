package com.kidd.uber_eat_demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.common.BaseContext;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.model.dto.OrderDto;
import com.kidd.uber_eat_demo.model.entity.OrderDetail;
import com.kidd.uber_eat_demo.model.entity.Orders;
import com.kidd.uber_eat_demo.service.OrderDetailService;
import com.kidd.uber_eat_demo.service.OrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 用户下单
     * 
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);

        orderService.submit(orders);

        return R.success("下单成功");
    }

    /**
     * 订单信息分页查询
     * 
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping({ "/page", "/userPage" })
    public R<Page<OrderDto>> page(HttpServletRequest request, int page, int pageSize, String number, String beginTime,
            String endTime) {

        Page<Orders> orderPage = new Page<>(page, pageSize);
        Long userId = null;
        String path = request.getRequestURI();

        // 用户请求
        if (path.substring(path.lastIndexOf("/")) == "userPage") {
            userId = BaseContext.getCurrentId();
        }

        LambdaQueryWrapper<Orders> queryWrapper = Wrappers.lambdaQuery(Orders.class);
        queryWrapper.eq(number != null, Orders::getNumber, number)
                .gt(beginTime != null, Orders::getOrderTime, beginTime)
                .lt(endTime != null, Orders::getOrderTime, endTime)
                .eq(userId != null, Orders::getUserId, userId)
                .orderByDesc(Orders::getOrderTime);

        orderService.page(orderPage, queryWrapper);

        if (orderPage.getRecords() == null) {
            return R.error("没有信息");
        }
        Page<OrderDto> orderDtoPage = new Page<>();
        BeanUtils.copyProperties(orderPage, orderDtoPage, "records");

        List<Orders> records = orderPage.getRecords();
        List<OrderDto> list = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            LambdaQueryWrapper<OrderDetail> wrapper = Wrappers.lambdaQuery(OrderDetail.class);
            wrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);
            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        orderDtoPage.setRecords(list);

        return R.success(orderDtoPage);
    }

    @GetMapping("/list")
    public R<List<Orders>> list() {

        LambdaQueryWrapper<Orders> queryWrapper = Wrappers.lambdaQuery(Orders.class);
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());

        return R.success(orderService.list(queryWrapper));
    }

}
