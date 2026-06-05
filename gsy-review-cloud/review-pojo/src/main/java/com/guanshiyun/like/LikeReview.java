package com.guanshiyun.like;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
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
@Table("like_review")
public class LikeReview extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id; // ID
    private Long reviewId; // 评论ID
    private Long tenantId; // 租户ID
}
