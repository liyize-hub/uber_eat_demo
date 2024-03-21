package com.kidd.uber_eat_demo.common;

public class BaseContext {
    // threadLocal是thread的局部变量,新建一个线程局部变量
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * 
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * 
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
