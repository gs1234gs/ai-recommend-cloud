package com.guanshiyun.controller.similarity.vo;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@FieldNameConstants
public class SimilarityThresholdStrategyConfigurationVO extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;                 // 商品ID

    //阈值
    private Double similarityThreshold;

}
