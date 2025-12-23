package com.guanshiyun.browse;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.rpc.profile.CategoryApiVO;
import com.guanshiyun.rpc.profile.ProductApiVO;
import com.guanshiyun.rpc.profile.SKUApiVO;
import com.guanshiyun.rpc.profile.TagApiVO;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Document("user_browse")
@Accessors(chain = true)
public class UserBrowseMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    @Id
    private BigInteger id;
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
    private BigInteger browseDuration;
    //分类
    private List<CategoryApiVO> categoryList;
    //sku列表
    private List<SKUApiVO> skuList;
    //标签
    private List<TagApiVO> tagList;

}
