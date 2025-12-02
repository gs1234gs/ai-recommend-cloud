package com.guanshiyun.source;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
/**
 * Gorse 推荐结果中的单个商品评分项。
 * 通常作为推荐接口（如 /api/recommend/{user_id}）返回列表中的元素。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class Score implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;/**
     * 商品的唯一标识符（ItemId）。
     * 对应你在 Item 中定义的 itemId。
     * Gorse 通过此 ID 告诉你：“推荐这个商品”。
     * 示例："iphone15", "kindle_paperwhite"
     */
    @JsonProperty("Id")
    private String id;
    /**
     * Gorse 为该商品计算的推荐分数（数值越大越推荐）。
     * 分数由多种策略融合得出，例如：
     *   - 协同过滤相似度
     *   - 热门程度（Popular）
     *   - 向量相似度（Embedding）
     *   - 最新程度（时间衰减）
     * 注意：分数无绝对单位，仅用于排序（相对大小有意义）。
     */
    @JsonProperty("Score")
    private double score;
}
