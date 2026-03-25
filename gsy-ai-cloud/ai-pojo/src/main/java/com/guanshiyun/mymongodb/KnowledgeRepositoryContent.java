package com.guanshiyun.mymongodb;

import com.guanshiyun.content.ContentText;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库
 * */
@Document("knowledge_repository_content")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
public class KnowledgeRepositoryContent {
    //知识库id
    @Id
    private Long id;
    //知识库内容
    private List<ContentText> contentTexts;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    public Long creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    public Long updater;
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
