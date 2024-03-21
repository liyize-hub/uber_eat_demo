package com.kidd.uber_eat_demo.common;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 */
@Slf4j
// 定义全局控制器通知
// 可以被用来定义全局性的异常处理、全局数据绑定、全局数据预处理等。
// 对所有的controller进行拦截
@ControllerAdvice(annotations = { RestController.class, Controller.class })
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * 处理sql相关异常
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    /**
     * 异常处理方法
     * 处理自定义业务异常
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
