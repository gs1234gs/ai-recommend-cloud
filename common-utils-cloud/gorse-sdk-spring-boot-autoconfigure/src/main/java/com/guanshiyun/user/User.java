package com.guanshiyun.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@FieldNameConstants
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("UserId")
    private String userId;
    @JsonProperty("Labels")
    private Object labels;
}
