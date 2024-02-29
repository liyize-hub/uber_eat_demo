package com.kidd.uber_eat_demo.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.dto.DishDto;
import com.kidd.uber_eat_demo.entity.Category;
import com.kidd.uber_eat_demo.entity.Dish;
import com.kidd.uber_eat_demo.service.CategoryService;
import com.kidd.uber_eat_demo.service.DishFlavorService;
import com.kidd.uber_eat_demo.service.DishService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/dish")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * 
     * @param dish
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * Mybatis+sql
     * 
     * @param page
     * @param size
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {

        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

        // Solution 1: 纯MP方法
        // // 构造分页构造器对象
        // Page<Dish> pageInfo = new Page<>(page, pageSize);
        // Page<DishDto> dishDtoPage = new Page<>();

        // // 条件构造器
        // LambdaQueryWrapper<Dish> queryWrapper = Wrappers.lambdaQuery(Dish.class);
        // queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        // // 添加排序条件
        // queryWrapper.orderByDesc(Dish::getUpdateTime);

        // // 执行分页查询
        // dishService.page(pageInfo, queryWrapper);

        // // 对象拷贝
        // BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // List<Dish> records = pageInfo.getRecords();
        // List<DishDto> list = records.stream().map((item) -> {
        // DishDto dishDto = new DishDto();
        // BeanUtils.copyProperties(item, dishDto);
        // Long categoryId = item.getCategoryId();
        // // 根据id查询分类对象
        // Category category = categoryService.getById(categoryId);
        // String categoryName = category.getName();
        // dishDto.setCategoryName(categoryName);
        // return dishDto;
        // }).collect(Collectors.toList());

        // dishDtoPage.setRecords(list);

        // Solution 2： Mybatis+sql
        Page<DishDto> dishdtoPage = new Page<>(page, pageSize);

        dishdtoPage = dishService.pageDishDto(dishdtoPage, name);

        return R.success(dishdtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 根据条件查询对应的菜品数据
     * mybatis+sql
     * 
     * @param param
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        log.info("dish:{}", dish);
        // // 条件构造器
        // LambdaQueryWrapper<Dish> queryWrapper = Wrappers.lambdaQuery(Dish.class);
        // queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId,
        // dish.getCategoryId());
        // // 添加条件，查询状态为1（起售状态）的菜品
        // queryWrapper.eq(Dish::getStatus, 1);
        // queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // List<Dish> dishList = dishService.list(queryWrapper);

        return R.success(dishService.getFullList(dish));
    }

    /**
     * 修改菜品
     * 
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 菜品禁用启用
     * 
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam("ids") List<Long> ids) {
        log.info("status:{},停售起售的ids：{}", status, ids);

        // 通过ids批量获取dish 菜品
        List<Dish> dishes = dishService.listByIds(ids);
        // 利用stream流，设置dish的status字段为 传过来的status值
        dishes = dishes.stream().map((item) -> {
            item.setStatus(status);
            return item;

        }).collect(Collectors.toList());

        // 批量修改
        boolean flag = dishService.updateBatchById(dishes);

        return flag ? R.success("操作成功") : R.error("操作失败");
    }

    /**
     * 菜品删除
     * 
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("需要删除的菜品的ids：{}", ids);
        boolean flag = dishService.removeByIdsWithFlavors(ids);
        return flag ? R.success("删除成功") : R.error("删除失败");
    }

}
