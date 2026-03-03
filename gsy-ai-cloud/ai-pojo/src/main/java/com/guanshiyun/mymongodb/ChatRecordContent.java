package com.guanshiyun.mymongodb;

import com.guanshiyun.content.ContentText;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
/**
 * 聊天记录
 * */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Document(collection = "chat_record_content")
public class ChatRecordContent {
    //聊天记录内容id
    @Id
    private BigInteger id;
    // 会话标题
    private String title;
    //发送方id
    private BigInteger senderId;
    //接收方id
    private BigInteger receiverId;
    //内容
    private List<ContentText> contentTexts;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    public BigInteger creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    public BigInteger updater;
    /**
     * 创建时间
     */
    public LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    public LocalDateTime updateTime;
    /**
     * 是否删除，删除标记,0-未删除，1-已删除
     */
    public short delFlag;
}
