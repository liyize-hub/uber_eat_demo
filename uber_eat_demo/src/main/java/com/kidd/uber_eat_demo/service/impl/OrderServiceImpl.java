package com.kidd.uber_eat_demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.common.BaseContext;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.entity.AddressBook;
import com.kidd.uber_eat_demo.entity.OrderDetail;
import com.kidd.uber_eat_demo.entity.Orders;
import com.kidd.uber_eat_demo.entity.ShoppingCart;
import com.kidd.uber_eat_demo.entity.User;
import com.kidd.uber_eat_demo.mapper.OrderMapper;
import com.kidd.uber_eat_demo.service.AddressBookService;
import com.kidd.uber_eat_demo.service.OrderDetailService;
import com.kidd.uber_eat_demo.service.OrderService;
import com.kidd.uber_eat_demo.service.ShoppingCartService;
import com.kidd.uber_eat_demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    UserService userService;

    @Autowired
    AddressBookService addressBookService;

    @Autowired
    OrderDetailService orderDetailService;

    @Override
    public void submit(Orders orders) {
        // A. 获得当前用户 id, 查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();

        // B. 根据当前登录用户 id, 查询购物车数据/用户数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }

        User user = userService.getById(userId);

        // C. 根据地址 ID, 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，不能下单");
        }
        // 使用 IdWorker 类提供的方法来生成唯一的ID
        long orderId = IdWorker.getId();// 订单号

        // 订单的实收金额
        // 当一个变量被 final 修饰时，表示该变量的值只能被赋值一次，之后不能再被修改
        // amount 是一个数组引用，它被声明为 final，意味着该引用只能指向一个数组对象，并且不能再指向其他的数组对象。
        // 但是，这并不意味着数组对象本身的内容不可变，而只是意味着 amount 这个引用不可变。
        // final int[] amount = { 0 };

        // AtomicInteger 是 Java 中的一个原子整型类，
        // 它提供了一种线程安全的方式来进行整数操作。
        // 在多线程环境下，使用 AtomicInteger 可以避免竞态条件（race condition）和不一致的操作，确保对整数的操作是原子的。
        AtomicInteger amount = new AtomicInteger(0);

        // D. 组装订单明细数据, 批量保存订单明细
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());
            // int permoney = item.getAmount().intValue() * item.getNumber();
            // 用于原子性地将给定的值添加到当前值，并返回相加后的结果。 +=
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());

        // E. 组装订单数据, 批量保存订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        // todo 金额是等于：订单详情表之和
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId)); // 订单号
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        // 向订单表插入数据，一条数据
        this.save(orders);

        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        // F. 删除当前用户的购物车列表数据
        shoppingCartService.remove(queryWrapper);
    }
}
