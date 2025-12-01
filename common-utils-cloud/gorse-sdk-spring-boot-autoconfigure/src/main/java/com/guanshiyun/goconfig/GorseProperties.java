package com.guanshiyun.goconfig;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@ConfigurationProperties(prefix = "gorse")
public class GorseProperties {
    private String url;
    private String apiKey;

}
