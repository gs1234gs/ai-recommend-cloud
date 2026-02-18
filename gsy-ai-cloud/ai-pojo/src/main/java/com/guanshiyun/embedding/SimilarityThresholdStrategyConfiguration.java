package com.guanshiyun.embedding;

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
import java.math.BigInteger;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@FieldNameConstants
@Table("similarity_threshold_strategy_configuration")
public class SimilarityThresholdStrategyConfiguration extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private BigInteger id;                 // 商品ID

    //阈值
    private Double similarityThreshold;

}
