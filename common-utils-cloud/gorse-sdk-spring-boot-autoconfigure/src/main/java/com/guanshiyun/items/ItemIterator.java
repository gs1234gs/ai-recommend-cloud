package com.guanshiyun.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Accessors(chain = true)
public class ItemIterator implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("Cursor")
    private String cursor;
    @JsonProperty("Items")
    private List<Item> items;
}
