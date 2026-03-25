package com.guanshiyun.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;



@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ReqChat {
    //会话id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long conversationId;
    //消息内容
    private String content;
    //是否是新对话
    private boolean flag = false;
}
