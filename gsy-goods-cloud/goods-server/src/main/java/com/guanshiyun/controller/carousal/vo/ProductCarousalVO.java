package com.guanshiyun.controller.carousal.vo;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProductCarousalVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private BigInteger id;
    //轮播标题
    private String title;
    //图片路径
    private String image;
    //轮播链接
    private String link;
    //轮播顺序
    private Integer sort;
    //是否显示，0不显示，1显示
    private Integer status;
    //描述
    private String description;
    //类型，1登录页轮播图，2商品详情轮播图
    private Integer type;
}
