package com.guanshiyun.review;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Accessors(chain = true)
@Table("review_product")
public class ReviewProduct extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //订单id
    @Id
    private Long id;
    //父级id
    private Long parentId;
    //评论内容
    private String content;
    //图片
    private String image;
    //商品id
    private Long productId;
    //租户id
    private Long tenantId;
}
