package com.guanshiyun.embedding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Document(collection = "embedding_product")
@FieldNameConstants
public class EmbeddingProduct {
    @Id
    private BigInteger id;                 // 商品ID

    private float[] embedding;       // PGVector 直接用 float[]

    private String summary;          // 用于 embedding 的文本

    private String contentHash;      // hash 判断是否要重建 embedding

    private LocalDateTime updatedAt; // 更新时间
}
