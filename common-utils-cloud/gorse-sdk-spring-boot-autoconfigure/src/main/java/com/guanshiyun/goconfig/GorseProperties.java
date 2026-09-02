package com.guanshiyun.goconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Accessors(chain = true)
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@ConfigurationProperties(prefix = "gorse")
public class GorseProperties {
    private String url;
    private String apiKey;

}
