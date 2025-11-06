package com.guanshiyun.search;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.profile.SearchContent;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldNameConstants
@ToString
@Document("user_search")
public class UserSearchMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //会话 id
    private BigInteger id;
    //搜索内容
    private List<SearchContent> searchContent;
}
