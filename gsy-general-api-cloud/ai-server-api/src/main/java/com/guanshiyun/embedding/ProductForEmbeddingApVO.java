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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
@Accessors(chain = true)
public class ProductForEmbeddingApVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private BigInteger id;           // 商品ID
    private String title;            // 商品标题
    private String description;      // 商品描述（不进 embedding）
    private String brand;            // 品牌
    private String placeOfOrigin;    // 产地（一般不进 embedding）
    private List<String> categoryNames;
    private List<String> tagNames;

    private List<SkuItem> skuList;   // SKU 信息（不进 embedding）// 当前价格（不进 embedding）
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
        Map<String,Object> metadata = new HashMap<>();
        putIfNotNull(metadata, Fields.id, id.toString());
        putIfNotNull(metadata, Fields.title, title);
        putIfNotNull(metadata, Fields.brand, brand);
        putIfNotNull(metadata, Fields.categoryNames, categoryNames);
        putIfNotNull(metadata, Fields.tagNames, tagNames);
        putIfNotNull(metadata, Fields.skuList, skuList);
        putIfNotNull(metadata, Fields.score, score);
        return metadata;
    }
    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (Objects.nonNull( value)) {
            map.put(key, value);
        }
    }
    // ================== SKU ==================

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Accessors(chain = true)
    public static class SkuItem {
        private String id;
        private String name;
        private String skuCode;
        private String price;
    }


}
