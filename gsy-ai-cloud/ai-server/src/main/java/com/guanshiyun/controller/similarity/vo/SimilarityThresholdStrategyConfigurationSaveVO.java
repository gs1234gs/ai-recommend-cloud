package com.guanshiyun.controller.similarity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SimilarityThresholdStrategyConfigurationSaveVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;                 // 商品ID

    //阈值
    private Double similarityThreshold;

}
