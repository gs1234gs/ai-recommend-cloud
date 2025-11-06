package com.guanshiyun.content;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;
/**
 * 文本结构
 * */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
public class ContentText {
    private BigInteger id;
   //接收内容
    private String receiverContent;
    //发送内容
    private String senderContent;
}
