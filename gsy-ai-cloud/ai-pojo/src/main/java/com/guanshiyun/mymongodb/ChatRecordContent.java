package com.guanshiyun.mymongodb;

import com.guanshiyun.base.BasePojo;
import com.guanshiyun.content.ContentText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
/**
 * 聊天记录
 * */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Document(collection = "chat_record_content")
public class ChatRecordContent extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //聊天记录内容id
    @Id
    private Long id;
    // 会话标题
    private String title;
    //发送方id
    private Long senderId;
    //接收方id
    private Long receiverId;
    //内容
    private List<ContentText> contentTexts;
}
