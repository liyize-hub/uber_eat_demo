package com.kidd.uber_eat_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kidd.uber_eat_demo.common.BaseContext;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.model.entity.AddressBook;
import com.kidd.uber_eat_demo.service.AddressBookService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/addressBook")
@RestController
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址信息
     * 
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        addressBookService.save(addressBook);

        return R.success("添加成功");
    }

    /**
     * 根据id查询地址
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        }
        return R.error("没找到该对象");
    }

    /**
     * 查询默认地址
     * 
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getdefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook != null) {
            return R.success(addressBook);
        }
        return R.error("没有找到该对象");
    }

    /**
     * 设置默认地址
     * 先将该用户所有地址的 is_default 更新为 0 ,
     * 然后将当前的设置的默认地址的 is_default 设置为 1
     *
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> queryWrapper = Wrappers.lambdaUpdate(AddressBook.class);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                .set(AddressBook::getIsDefault, 0);
        // SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(queryWrapper);

        addressBook.setIsDefault(1);
        // SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功");
    }

    /**
     * 查询指定用户的全部地址
     * 
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        LambdaQueryWrapper<AddressBook> queryWrapper = Wrappers.lambdaQuery(AddressBook.class);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 修改地址
     * 
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        boolean flag = addressBookService.updateById(addressBook);
        return flag ? R.success("修改成功") : R.error("修改失败");
    }

    /**
     * 删除地址
     * 
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam("ids") List<Long> ids) {
        boolean isdelete = addressBookService.removeByIds(ids);
        return isdelete ? R.success("删除成功") : R.error("删除失败");
    }

}
