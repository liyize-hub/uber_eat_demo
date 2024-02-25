package com.kidd.uber_eat_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j // 使用log输出日志
@ServletComponentScan // 启动组件扫描 过滤器
@EnableTransactionManagement // 开启事务支持
public class UberEatDemoApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(UberEatDemoApplication.class, args);
		log.info("项目启动成功。。。");
	}
}
