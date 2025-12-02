package com.guanshiyun.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
/**
 * Gorse 推荐系统中的商品（Item）数据模型。
 * 用于向 Gorse 同步商品元信息，支持标签、类目、描述、时间戳等，
 * 是实现内容理解、冷启动推荐和过滤召回的基础。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 商品的唯一标识符（必须全局唯一）。
     * Gorse 通过此 ID 关联用户行为（如点击、购买）和推荐结果。
     * 示例："prod_1001"、"sku_iphone15"
     */
    @JsonProperty("ItemId")
    private String itemId;
    /**
     * 是否隐藏该商品（默认 false）。
     * 设为 true 时，该商品不会出现在任何推荐列表中，
     * 但历史行为数据仍保留（适用于下架、违规或测试商品）。
     */
    @JsonProperty("IsHidden")
    private Boolean isHidden;
    /**
     * 商品的标签列表（灵活、非结构化）。
     * 用于基于标签的过滤、规则召回或相似匹配。
     * 可包含品牌、属性、活动等信息，如：["Apple", "5G", "新品", "防水"]。
     */
    @JsonProperty("Labels")
    private List<String> labels;
    /**
     * 商品所属的类目路径（支持多级类目）。
     * 建议按从粗到细的顺序排列，用于类目内热门推荐或层级过滤。
     * 示例：["电子产品", "手机", "智能手机"]
     */
    @JsonProperty("Categories")
    private List<String> categories;
    /**
     * 商品的时间戳（创建或上新时间），格式为 ISO 8601（如 "2025-12-01T10:30:00Z"）。
     * Gorse 可据此实现“时间衰减”策略（新商品权重更高），
     * 对冷启动和时效性推荐非常重要。
     */
    @JsonProperty("Timestamp")
    private String timestamp;
    io.gorse.gorse4j.Item
    /**
     * 商品的描述性文本（自由文本）。
     * 虽然 Gorse 本身不直接解析语义，但该字段常被用于：
     *   - 人工调试查看商品信息
     *   - 外部系统（如你）用大模型（Qwen）生成 embedding 的原始输入
     * 建议包含名称、核心卖点、关键属性等。
     * 示例："iPhone 15 Pro 256GB 深空黑，A17芯片，支持USB-C"
     */
    @JsonProperty("Comment")
    private String comment;
}
