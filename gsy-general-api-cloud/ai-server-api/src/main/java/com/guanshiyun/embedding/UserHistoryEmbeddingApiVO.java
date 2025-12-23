package com.guanshiyun.embedding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@FieldNameConstants
public class UserHistoryEmbeddingApiVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private BigInteger id;
}
