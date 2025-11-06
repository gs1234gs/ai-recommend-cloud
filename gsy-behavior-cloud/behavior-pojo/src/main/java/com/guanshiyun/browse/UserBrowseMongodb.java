package com.guanshiyun.browse;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.profile.BrowseProfile;
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
@ToString
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Document("user_browse")
public class UserBrowseMongodb extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id，即会话id
    private BigInteger id;
    //浏览内容
    private List<BrowseProfile> browseContent;
    //ip地址
    private String ipAddress;
    //设备类型
    private String deviceType;
    //浏览时长,单位毫秒
    private BigInteger browseDuration;
}
