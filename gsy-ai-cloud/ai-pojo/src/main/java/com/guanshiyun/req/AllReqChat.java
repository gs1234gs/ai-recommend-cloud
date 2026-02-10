package com.guanshiyun.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AllReqChat {
    //会话id
    @JsonSerialize(using = ToStringSerializer.class)
    private BigInteger conversationId;
    //消息内容
    private String content;
    //商品id列表
    private List<BigInteger> productIdList;
}
