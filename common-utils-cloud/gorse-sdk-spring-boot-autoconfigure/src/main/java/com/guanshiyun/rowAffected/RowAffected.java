package com.guanshiyun.rowAffected;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Accessors(chain = true)
@EqualsAndHashCode
public class RowAffected implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("RowAffected")
    private int rowAffected;
}
