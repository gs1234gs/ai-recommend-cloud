package com.guanshiyun.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
/**
 * Gorse 推荐系统中的用户（User）实体类。
 *
 * 该类用于：
 *   - 向 Gorse 注册新用户；
 *   - 从 Gorse 获取用户信息（含标签）；
 *   - 构建带用户画像的推荐请求。
 *
 * ️ Gorse 要求：
 *   - 每个用户必须有唯一的 `UserId`；
 *   - `Labels` 是可选的用户标签集合，用于基于内容的过滤或冷启动推荐；
 *   - 所有字段名必须为 PascalCase（与 Gorse REST API 严格一致）。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@FieldNameConstants
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识符（User ID）。
     *
     * 要求：
     *   - 非空、全局唯一；
     *   - 不能包含特殊字符（建议使用字母、数字、下划线或连字符）；
     *   - 一旦设定，不可更改（Gorse 内部以此作为主键）。
     *
     * 示例："user_123", "U-2025-001", "alice@example.com"
     */
    @JsonProperty("UserId")
    private String userId;
    /**
     * 用户标签（Labels），用于描述用户属性或兴趣。
     *
     * Gorse 要求：
     *   - 类型为字符串数组（string[]）或对象（object）；
     *   - 常见形式：["student", "male", "tech-lover"] 或 {"age": "25", "city": "Beijing"}；
     *   - 在 REST API 中以 JSON 对象或数组形式传输。
     *
     * 当前定义为 {@code Object} 是为了兼容两种格式，但会牺牲类型安全。
     *    建议根据实际使用方式明确类型（见下方【改进建议】）。
     *
     * 示例 JSON：
     *   { "Labels": ["vip", "mobile_user"] }
     *   或
     *   { "Labels": { "tier": "gold", "source": "app" } }
     */
    @JsonProperty("Labels")
    private Object labels;
}
