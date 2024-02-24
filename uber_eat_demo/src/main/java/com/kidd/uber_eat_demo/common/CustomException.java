package com.kidd.uber_eat_demo.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
