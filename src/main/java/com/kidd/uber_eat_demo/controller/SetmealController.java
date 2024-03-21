package com.kidd.uber_eat_demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.model.dto.SetmealDto;
import com.kidd.uber_eat_demo.model.entity.Setmeal;
import com.kidd.uber_eat_demo.service.SetmealDishService;
import com.kidd.uber_eat_demo.service.SetmealService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CacheManager cacheManager;

    @PostMapping
    @ApiOperation(value = "新增套餐接口")
    @CacheEvict(value = "setmealCache", allEntries = true) // 清除setmealCache名称下,所有的缓存数据
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * Mybatis+sql
     * 
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    // 用在@ApiImplicitParams 注解中，指定一个请求参数的各个方面的属性
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "套餐名称", required = false)
    })
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

        Page<SetmealDto> SetmealDtoPage = new Page<>(page, pageSize);

        SetmealDtoPage = setmealService.pageDishDto(SetmealDtoPage, name);

        return R.success(SetmealDtoPage);
    }

    /**
     * 套餐禁用启用
     * 
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam("ids") List<Long> ids) {
        log.info("status:{},停售起售的ids：{}", status, ids);

        // 通过ids批量获取dish 菜品
        List<Setmeal> setmeals = setmealService.listByIds(ids);
        // 利用stream流，设置dish的status字段为 传过来的status值
        setmeals = setmeals.stream().map((item) -> {
            item.setStatus(status);
            return item;

        }).collect(Collectors.toList());

        // 批量修改
        boolean flag = setmealService.updateBatchById(setmeals);

        return flag ? R.success("操作成功") : R.error("操作失败");
    }

    /**
     * 根据id查询套餐信息和对应的菜品信息
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {

        SetmealDto setmealDto = setmealService.getByIdWithDishes(id);

        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * 
     * @param setmealhDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());

        setmealService.updateWithDishes(setmealDto);

        return R.success("修改套餐成功");
    }

    /**
     * 套餐删除
     * 
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true) // 清除setmealCache名称下,所有的缓存数据
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("需要删除的套餐的ids：{}", ids);
        boolean flag = setmealService.removeByIdsWithDishes(ids);
        return flag ? R.success("删除成功") : R.error("删除失败");
    }

    /**
     * 根据条件查询套餐数据
     * `#setmeal.categoryId` : #setmeal 指的是方法形参的名称, categoryId 指的是 setmeal 的
     * categoryId 属性 ,
     * 也就是使用 setmeal 的 categoryId和status拼接 属性作为 key ;
     * 
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper = Wrappers.lambdaQuery(Setmeal.class);
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);

        return R.success(setmealService.list(queryWrapper));
    }

}
