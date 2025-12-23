package com.guanshiyun.rpc.chatrecommend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class AiChatClientRecommendApiVO {
    //商品名称
    private String productName;
    //销量
    private Integer sales;
    //价格
    private Double price;
    //商品id
    private BigInteger productId;
    //

}
