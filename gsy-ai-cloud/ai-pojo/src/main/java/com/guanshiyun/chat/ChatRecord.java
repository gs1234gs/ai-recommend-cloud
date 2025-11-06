package com.guanshiyun.chat;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 聊天记录
 * */


@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@Builder
@Table("chat_record")
public class ChatRecord {
    //聊天记录id
    @Id
    private BigInteger id;
    //标题
    private String title;
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
