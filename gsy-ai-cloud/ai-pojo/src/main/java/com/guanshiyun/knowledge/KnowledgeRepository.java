package com.guanshiyun.knowledge;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 知识库
 * */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@Table("knowledge_repository")
public class KnowledgeRepository {
    @Id
    private BigInteger id;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    public BigInteger creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    public BigInteger updater;
    /**
     * 创建时间
     */
    public LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    public LocalDateTime updateTime;
    /**
     * 是否删除，删除标记,0-未删除，1-已删除
     */
    public short delFlag;


}
