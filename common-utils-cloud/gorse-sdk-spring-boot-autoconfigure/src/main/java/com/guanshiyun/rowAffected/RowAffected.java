package com.guanshiyun.rowAffected;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
/**
 * Gorse 推荐系统 API 操作结果的响应模型，表示受影响的记录行数。
 *
 * 该类通常作为以下操作的返回值：
 *   - 批量插入用户（/api/user）；
 *   - 批量插入反馈（/api/feedback）；
 *   - 批量插入物品（/api/item）；
 *   - 删除用户/物品/反馈等写操作。
 *
 * Gorse REST API 示例响应：
 *   { "RowAffected": 5 }
 *
 * 此类的作用是将上述 JSON 反序列化为 Java 对象，便于 SDK 使用者获取操作结果。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Accessors(chain = true)
@EqualsAndHashCode
@FieldNameConstants
public class RowAffected implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 被操作影响的记录数量。
     *
     * 含义示例：
     *   - 插入 10 条反馈，其中 8 条是新数据 → rowAffected = 8；
     *   - 删除不存在的用户 → rowAffected = 0；
     *   - 更新 3 个物品标签 → rowAffected = 3。
     *
     *  注意：
     *   - Gorse 的“插入”操作默认会跳过已存在的主键（如重复的 (userId, itemId, feedbackType)），
     *     因此 rowAffected ≤ 请求中的条目数；
     *   - 该值为非负整数（>= 0），不会为负。
     */
    @JsonProperty("RowAffected")
    private int rowAffected;
}
