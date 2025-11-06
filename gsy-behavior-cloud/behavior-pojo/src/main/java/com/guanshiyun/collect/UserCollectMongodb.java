package com.guanshiyun.collect;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.profile.CollectProfile;
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
@ToString(callSuper = true)
@Document("user_collect")
public class UserCollectMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //会话 id
    private BigInteger id;
    //会话内容
    private List<CollectProfile> collectContent;
}
