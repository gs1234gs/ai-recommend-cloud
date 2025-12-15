package com.guanshiyun.embedding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
@Accessors(chain = true)
public class ProductForEmbeddingApVO {
    private BigInteger id;           // 商品ID
    private String title;            // 商品标题
    private String description;      // 商品描述（不进 embedding）
    private String brand;            // 品牌
    private String placeOfOrigin;    // 产地（一般不进 embedding）
    private List<String> categoryNames;
    private List<String> tagNames;

    private List<SkuItem> skuList;   // SKU 信息（不进 embedding）
    private BigDecimal price;        // 当前价格（不进 embedding）
    //权重分数
    private double score;

    // ================== 推荐系统核心方法 ==================

    /**
     * 用于【推荐系统 Item Embedding】
     * 只包含稳定、不随交易变化的兴趣语义
     */
    public String recommendEmbeddingText() {
        StringBuilder sb = new StringBuilder();

        if (title != null) sb.append(title).append(" ");
        if (brand != null) sb.append(brand).append(" ");

        if (categoryNames != null && !categoryNames.isEmpty()) {
            sb.append(String.join(" ", categoryNames)).append(" ");
        }

        if (tagNames != null && !tagNames.isEmpty()) {
            sb.append(String.join(" ", tagNames));
        }

        return sb.toString().trim();
    }

    /**
     * 用于 VectorStore / Qdrant 的 metadata
     */
    public Map<String, Object> metadata() {
        return Map.of(
                Fields.brand, brand,
                Fields.categoryNames, categoryNames,
                Fields.tagNames, tagNames,
                Fields.price, price,
                Fields.score, score
        );
    }

    // ================== SKU ==================

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Accessors(chain = true)
    public static class SkuItem {
        private BigInteger id;
        private String name;
        private String skuCode;
        private BigDecimal price;
    }


}
