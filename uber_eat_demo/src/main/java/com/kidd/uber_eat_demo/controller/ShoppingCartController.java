package com.kidd.uber_eat_demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kidd.uber_eat_demo.common.BaseContext;
import com.kidd.uber_eat_demo.common.CustomException;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.entity.ShoppingCart;
import com.kidd.uber_eat_demo.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;

// 购物车
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * 
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

        if (shoppingCart.getDishId() == null && shoppingCart.getSetmealId() == null) {
            return R.error("dishid和setmealid都为空，信息不全");
        }

        ShoppingCart cartSerivceOne = shoppingCartService.modifyOne(shoppingCart, 1);

        return R.success(cartSerivceOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车中商品数量减一, 购物车数据:{}", shoppingCart);
        if (shoppingCart.getDishId() == null && shoppingCart.getSetmealId() == null) {
            return R.error("dishid和setmealid都为空，信息不全");
        }
        ShoppingCart cartSerivceOne = shoppingCartService.modifyOne(shoppingCart, -1);

        return R.success(cartSerivceOne);
    }

    /**
     * 查看购物车
     * 
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车。。。");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .orderByAsc(ShoppingCart::getCreateTime);

        return R.success(shoppingCartService.list(queryWrapper));
    }

    /**
     * 清空购物车
     * 
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        // SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = Wrappers.lambdaQuery(ShoppingCart.class);
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }

}
