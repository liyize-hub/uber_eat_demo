package com.example.uber_eat_demo.uber_eat_demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kidd.uber_eat_demo.UberEatDemoApplication;
import com.kidd.uber_eat_demo.model.entity.User;
import com.kidd.uber_eat_demo.service.UserService;

@SpringBootTest(classes = UberEatDemoApplication.class)
public class SpringCache {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    /**
     * CachePut：将方法返回值放入缓存
     * value：缓存的名称，每个缓存名称下面可以有多个key, 一类的缓存
     * key：缓存的key
     */
    @CachePut(value = "userCache", key = "#user.id") // 支持SPEL #result.id #root.methodName #root.caches #方法参数名user
    @PostMapping
    public User save(User user) {
        userService.save(user);
        return user;
    }

    /**
     * CacheEvict：清理指定缓存
     * value：缓存的名称，每个缓存名称下面可以有多个key
     * key：缓存的key
     */
    @CacheEvict(value = "userCache", key = "#p0") // #p0 代表第一个参数
    // @CacheEvict(value = "userCache",key = "#root.args[0]") //#root.args[0]
    // 代表第一个参数
    // @CacheEvict(value = "userCache",key = "#id") //#id 代表变量名为id的参数
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.removeById(id);
    }

    // @CacheEvict(value = "userCache",key = "#p0.id") //第一个参数的id属性
    // @CacheEvict(value = "userCache",key = "#user.id") //参数名为user参数的id属性
    // @CacheEvict(value = "userCache",key = "#root.args[0].id") //第一个参数的id属性
    @CacheEvict(value = "userCache", key = "#result.id") // 返回值的id属性
    @PutMapping
    public User update(User user) {
        userService.updateById(user);
        return user;
    }

    /**
     * Cacheable：在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；
     * 若没有数据，调用方法并将方法返回值放到缓存中
     * value：缓存的名称，每个缓存名称下面可以有多个key
     * key：缓存的key
     * condition：条件，满足条件时才缓存数据
     * unless：满足条件则不缓存
     */
    @GetMapping("/{id}")
    @Cacheable(value = "userCache", key = "#id", unless = "#result == null")
    public User getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return user;
    }

    @GetMapping("/list")
    @Cacheable(value = "userCache", key = "#user.id + '_' + #user.name")
    public List<User> list(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(user.getId() != null, User::getId, user.getId());
        queryWrapper.eq(user.getName() != null, User::getName, user.getName());
        List<User> list = userService.list(queryWrapper);
        return list;
    }

}
