package com.guanshiyun.feedback;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@FieldNameConstants
@EqualsAndHashCode(exclude = "value")
@Accessors(chain = true)
/**
 * Gorse 推荐系统 SDK 中的用户反馈（Feedback）实体类。
 *
 * 该类用于向 Gorse 服务提交用户行为日志（如点击、点赞、阅读等），
 * 也是从 Gorse 获取反馈数据时的反序列化模型。
 *
 * 注意：
 * - Gorse 原生仅支持隐式反馈（无评分值），但本 SDK 扩展了 `value` 字段以支持加权反馈或显式评分。
 * - 所有字段名通过 @JsonProperty 映射为 PascalCase，严格匹配 Gorse REST API 的 JSON 格式。
 * - 时间戳必须为 ISO 8601 格式（如 "2025-12-15T20:00:00Z"），否则 Gorse 可能拒绝写入。
 */
public class Feedback implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 反馈类型（Feedback Type），由用户在 Gorse 中预定义。
     * 常见值示例：
     *   - "click"    : 点击
     *   - "like"     : 点赞
     *   - "read"     : 阅读完成
     *   - "share"    : 分享
     *   - "purchase" : 购买
     *
     * 必须与 Gorse 配置中的 feedback_types 一致，否则可能被忽略。
     */
    @JsonProperty("FeedbackType")
    private String feedbackType;

    /**
     * 用户唯一标识符（User ID）。
     * Gorse 不校验格式，但需保证全局唯一且非空。
     */
    @JsonProperty("UserId")
    private String userId;
    /**
     * 物品唯一标识符（Item ID）。
     * 对应 Gorse 中的 item id，需与物品元数据中的 id 一致。
     */
    @JsonProperty("ItemId")
    private String itemId;

    /**
     * 【SDK 扩展字段】反馈的数值权重（非 Gorse 原生字段）。
     *
     * 用途：
     *   - 若使用显式评分（如 1~5 星），可将评分归一化到 [0,1] 区间后存入；
     *   - 在客户端计算反馈强度（如观看时长占比）；
     *   - 向 Gorse 提交时，此字段会被忽略（除非你修改了 Gorse 源码支持 value）。
     *
     * 注意：@EqualsAndHashCode 排除了此字段，确保相同行为（即使评分不同）视为同一反馈，
     *       符合 Gorse 的“行为去重”语义（同一用户对同一物品的同类型反馈只保留最新一条）。
     */
    @JsonProperty("Value")
    private double value;
    /**
     * 反馈发生的时间戳，必须为 ISO 8601 格式（UTC 时间）。
     * 示例："2025-12-15T20:00:00Z"
     *
     * Gorse 依赖此字段进行时间窗口过滤、新鲜度计算等。
     *     若未提供，Gorse 会使用服务器接收时间，可能导致时序混乱。
     */
    @JsonProperty("Timestamp")
    private String timestamp;
}
