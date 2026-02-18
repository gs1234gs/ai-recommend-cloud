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
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@Table("active_similarity_threshold_configuration")
public class ActiveSimilarityThresholdConfiguration extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private BigInteger id;
    //阈值
    private Double similarityThreshold;

}
