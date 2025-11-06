package com.guanshiyun.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqChat {
    //会话id
    private BigInteger conversationId;
    //消息内容
    private String content;
}
