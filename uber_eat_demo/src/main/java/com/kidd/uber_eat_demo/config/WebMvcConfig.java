package com.kidd.uber_eat_demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.kidd.uber_eat_demo.common.JacksonObjectMapper;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableSwagger2
@EnableKnife4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射
     * 
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射。。。");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展mvc框架的消息转换器
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");

        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到mvc框架的转换器集合中, 优先级为第一位
        converters.add(0, messageConverter);
    }

    // // bean的name一定要是：multipartResolver
    // // 要么 @Bean("multipartResolver") 注解中指定名字
    // // 要么方法的名字为：multipartResolver
    // @Bean("multipartResolver")
    // public CommonsMultipartResolver CommonsMultipartResolver() {
    // CommonsMultipartResolver cmr = new CommonsMultipartResolver();
    // // 最大的上传文件尺寸 50M
    // cmr.setMaxUploadSize(1024 * 1024 * 50);
    // // 每个的上传文件尺寸 10
    // cmr.setMaxUploadSizePerFile(1024 * 1024 * 10);
    // return cmr;
    // }

    @Bean
    public Docket createRestApi() {
        // 文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.kidd.uber_eat_demo.controller")) // 指定了包路径，表示只扫描该包下的控制器类。
                .paths(PathSelectors.any()) // 方法指定需要生成 API 文档的路径
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("uber_eat_demo")
                .version("1.0")
                .description("uber_eat_demo接口文档")
                .build();
    }
}
