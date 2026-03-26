package com.guanshiyun.postpojo;

import com.guanshiyun.base.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;


/**
 * 岗位实体
 * */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_post")
public class SysPost extends BasePojo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    //备注
    private String remark;

}
