package com.guanshiyun.click;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.profile.ClickProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@Document("user_click")
public class UserClickMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id
    private BigInteger id;
    //点击内容
    private List<ClickProfile> clickContent;
}
