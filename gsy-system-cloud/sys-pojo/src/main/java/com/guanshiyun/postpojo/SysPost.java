package com.guanshiyun.postpojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;



/**
 * 岗位实体
 * */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_post")
public class SysPost {

    //岗位ID
    @Id
    private Long id;
    //岗位代号
    private String code;
    //岗位名称
    private String name;
    //显示顺序
    private int sort;
    //岗位状态
    private short status;
    //创建者id
    private Long creatorId;
    //创建时间
    private String createTime;
    //更新者id
    private Long updaterId;
    //更新时间
    private String updateTime;
    //删除标识,0-正常,1-删除
    private short delFlag;
    //备注
    private String remark;

}
