package com.guanshiyun.controller.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;


import java.time.LocalDateTime;

/**
 * 聊天记录
 * */


@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@Builder
@Accessors(chain = true)
public class ChatRecordSaveVO {
    //聊天记录id
    private Long id;
    //标题
    private String title;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    public Long creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    public Long updater;
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
