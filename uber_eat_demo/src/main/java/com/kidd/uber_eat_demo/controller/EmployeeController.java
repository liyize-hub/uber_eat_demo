package com.kidd.uber_eat_demo.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.entity.Employee;
import com.kidd.uber_eat_demo.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * 
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    // json形式接收 ➕注解requestBody
    // HttpServletRequest 用于获取HTTP请求的各种信息，包括请求头、请求参数、请求体等
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        log.info("员工请求登陆...");

        // 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名username查询数据库
        // LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery(Employee.class);
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper); // IService 实现

        // 3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("找不到用户，登陆失败");
        }

        // 4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码不对，请重试");
        }

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus().equals(0)) {
            return R.error("账号已禁用");
        }

        // 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * 
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());

        // 1. 设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 2. 获得当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        // 向数据库中插入或更新一个实体对象, IService接口实现
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * 
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    // Page为MyBatis Plus提供，用于封装分页查询的参数和结果
    public R<Page<Employee>> page(int page, int pageSize, String name) {

        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

        // 1. 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 2. 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery(Employee.class);
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 3. 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * 
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息,编辑员工信息回显
     *
     * @param param
     * @return
     */
    @GetMapping("/{id}")
    // @PathVariable 从请求的 URL 中提取变量值。它通常用于处理 RESTful API 中的路径参数,并作为方法参数使用
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");

        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}
