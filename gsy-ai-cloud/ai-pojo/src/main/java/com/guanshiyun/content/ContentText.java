package com.guanshiyun.content;

import lombok.*;
import lombok.experimental.FieldNameConstants;


/**
 * 文本结构
 * */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
public class ContentText {
    private Long id;
   //接收内容
    private String receiverContent;
    //发送内容
    private String senderContent;
}
