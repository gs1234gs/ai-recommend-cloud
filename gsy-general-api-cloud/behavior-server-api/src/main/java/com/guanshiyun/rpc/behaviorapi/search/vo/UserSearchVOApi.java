package com.guanshiyun.rpc.behaviorapi.search.vo;

import com.guanshiyun.rpc.profile.SearchContentApi;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
public class UserSearchVOApi {
    //会话 id
    private BigInteger id;
    //搜索内容
    private List<SearchContentApi> searchContent;
}
