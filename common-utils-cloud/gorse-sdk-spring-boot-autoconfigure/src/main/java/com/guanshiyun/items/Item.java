package com.guanshiyun.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
//
// Gorse 推荐系统中的物品（Item）数据模型
// 用于向 Gorse 服务插入、更新或查询物品信息
//
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
    @JsonProperty("ItemId")
    private String itemId;
    @JsonProperty("IsHidden")
    private Boolean isHidden;
    @JsonProperty("Labels")
    private List<String> labels;
    @JsonProperty("Categories")
    private List<String> categories;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("Comment")
    private String comment;
}
