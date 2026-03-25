package com.guanshiyun.controller.browse.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guanshiyun.profile.ProductApiVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;

import java.time.LocalDateTime;

@Data
@Builder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBrowseSaveVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    private Long id;
    //商品id

    private ProductApiVO product;
    //浏览开始时间
    private LocalDateTime browseStartTime;
    //浏览结束时间
    private LocalDateTime browseEndTime;
    //ip地址
    private String ipAddress;
    //设备类型
    private String deviceType;
    //浏览时长,单位毫秒
    private Long browseDuration;
}
