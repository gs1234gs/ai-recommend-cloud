package com.guanshiyun.controller.search.vo;

import com.guanshiyun.profile.SearchContent;
import lombok.*;

import java.math.BigInteger;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
public class UserSearchVO {
    //会话 id
    private BigInteger id;
    //搜索内容
    private List<SearchContent> searchContent;
}
