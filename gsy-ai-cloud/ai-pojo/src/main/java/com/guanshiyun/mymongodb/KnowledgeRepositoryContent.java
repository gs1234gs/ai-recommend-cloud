package com.guanshiyun.mymongodb;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.content.ContentText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 知识库
 * */
@EqualsAndHashCode(callSuper = true)
@Document("knowledge_repository_content")
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
@FieldNameConstants
public class KnowledgeRepositoryContent extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //知识库id
    @Id
    private Long id;
    //知识库内容
    private List<ContentText> contentTexts;
}
