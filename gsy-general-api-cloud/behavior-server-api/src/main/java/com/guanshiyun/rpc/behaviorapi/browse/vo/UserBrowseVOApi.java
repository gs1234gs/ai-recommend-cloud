package com.guanshiyun.rpc.behaviorapi.browse.vo;


import com.guanshiyun.rpc.profile.BrowseProfileApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
public class UserBrowseVOApi implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //主键id，即会话id
    private BigInteger id;
    //浏览内容
    private List<BrowseProfileApi> browseContent;
    //ip地址
    private String ipAddress;
    //设备类型
    private String deviceType;
    //浏览时长,单位毫秒
    private BigInteger browseDuration;
}
