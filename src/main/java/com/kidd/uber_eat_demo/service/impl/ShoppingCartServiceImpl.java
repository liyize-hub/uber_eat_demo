package com.kidd.uber_eat_demo.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.common.BaseContext;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.mapper.ShoppingCartMapper;
import com.kidd.uber_eat_demo.model.entity.ShoppingCart;
import com.kidd.uber_eat_demo.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
                implements ShoppingCartService {

        @Override
        public ShoppingCart modifyOne(ShoppingCart shoppingCart, int flag) {
                log.info("商品数量{}, 购物车数据:{}", flag, shoppingCart);

                // 设置用户id，指定当前是哪个用户的购物车数据
                Long currentId = BaseContext.getCurrentId();
                shoppingCart.setUserId(currentId);

                // 查询当前菜品或者套餐是否在购物车中
                LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
                queryWrapper.eq(ShoppingCart::getUserId, currentId)
                                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId,
                                                shoppingCart.getSetmealId());

                // 查询当前菜品或者套餐是否在购物车中
                // SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
                ShoppingCart cartSerivceOne = this.getOne(queryWrapper);
                if (flag == 1) {
                        if (cartSerivceOne != null) {
                                // 如果已经存在，就在原来数量基础上加一
                                cartSerivceOne.setNumber(cartSerivceOne.getNumber() + 1);
                                this.updateById(cartSerivceOne);
                        } else {
                                // 如果不存在，则添加到购物车，数量默认就是1
                                shoppingCart.setNumber(1);
                                shoppingCart.setCreateTime(LocalDateTime.now());
                                this.save(shoppingCart);
                                cartSerivceOne = shoppingCart;
                        }
                } else if (flag == -1) {
                        if (cartSerivceOne != null) {
                                Integer number = cartSerivceOne.getNumber();
                                // 如果只剩最后一个，删除
                                if (number == 1) {
                                        this.remove(queryWrapper);
                                        cartSerivceOne.setNumber(0);
                                } else {
                                        // 如果已经存在，就在原来数量基础上减一
                                        cartSerivceOne.setNumber(cartSerivceOne.getNumber() - 1);
                                        this.updateById(cartSerivceOne);
                                }
                        } else {
                                throw new CustomException("数量为0, 不能再减了");
                        }
                }

                return cartSerivceOne;
        }
}
