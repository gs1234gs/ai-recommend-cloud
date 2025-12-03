package com.guanshiyun.goconfig;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldNameConstants
@Accessors(chain = true)
@ConfigurationProperties(prefix = "gorse")
public class GorseProperties {
    private String url;
    private String apiKey;

}
