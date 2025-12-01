package com.guanshiyun.source;

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
@EqualsAndHashCode
@Accessors(chain = true)
public class Score implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Score")
    private double score;
}
