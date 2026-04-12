package com.guanshiyun.chat;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;

/**
 * 聊天记录
 * */


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@SuperBuilder(toBuilder = true)
@Table("chat_record")
public class ChatRecord extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //聊天记录id
    @Id
    private Long id;
    //标题
    private String title;
}
