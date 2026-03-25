package com.guanshiyun.rpc.apisave;


import com.guanshiyun.profile.ProductApiVO;
import lombok.*;
import lombok.experimental.FieldNameConstants;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@FieldNameConstants
public class UserClickSaveApiVO {
    private Long id;
    //点击时间
    private LocalDateTime clickTime;
    //点击标签id
    private Long tagId;
    //点击
    private ProductApiVO product;
}
