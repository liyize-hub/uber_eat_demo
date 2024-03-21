package com.kidd.uber_eat_demo.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐
 */
@Data
@ApiModel("套餐")
// Serializable 标识了一个类的对象可以被序列化，即可以将对象转换成字节流，从而可以在网络上传输或者保存到文件中
public class Setmeal implements Serializable {

    // 是 Java 中用于版本控制的一个特殊字段，它是一个
    // long 类型的常量，用于标识序列化类的版本号
    // 即使类的其他部分发生了变化，版本号仍然保持不变，就可以确保序列化和反序列化的兼容性
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    // 分类id
    @ApiModelProperty("分类id")
    private Long categoryId;

    // 套餐名称
    @ApiModelProperty("套餐名称")
    private String name;

    // 套餐价格
    @ApiModelProperty("套餐价格")
    private BigDecimal price;

    // 状态 0:停用 1:启用
    @ApiModelProperty("状态")
    private Integer status;

    // 编码
    @ApiModelProperty("套餐编号")
    private String code;

    // 描述信息
    @ApiModelProperty("描述信息")
    private String description;

    // 图片
    @ApiModelProperty("图片")
    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
