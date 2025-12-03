package com.guanshiyun.feedback;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@FieldNameConstants
@EqualsAndHashCode
@Accessors(chain = true)
public class Feedback implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("FeedbackType")
    private String feedbackType;
    @JsonProperty("UserId")
    private String userId;
    @JsonProperty("ItemId")
    private String itemId;
    @JsonProperty("Timestamp")
    private String timestamp;
}
