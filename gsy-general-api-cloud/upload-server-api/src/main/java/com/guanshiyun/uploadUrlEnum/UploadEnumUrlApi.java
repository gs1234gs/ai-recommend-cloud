package com.guanshiyun.uploadUrlEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadEnumUrlApi {
    UPLOAD_IMAGE_URL("上传多个文件，返回多地址","/upload/image"),
    UPLOAD_URL("上传单个文件，返回单地址","/upload/upload");
    private final String name;
    private final String value;

}
