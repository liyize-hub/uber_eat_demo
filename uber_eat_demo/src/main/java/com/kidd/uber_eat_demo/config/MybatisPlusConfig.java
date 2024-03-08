package com.kidd.uber_eat_demo.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置MP的分页插件
 */
// 标识一个类作为应用程序上下文的配置类
// 可以使用 @Bean 注解来声明各种Spring容器管理的Bean。
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // PaginationInnerInterceptor 是 MyBatis-Plus 提供的一个内置拦截器，
        // 用于在查询中自动添加分页信息，简化分页查询的处理
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // MP的分页插件
        return mybatisPlusInterceptor;
    }
}
