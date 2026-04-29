package com.guanshiyun.zmoudle.dto.impl;

import io.gorse.gorse4j.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Items implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String itemId;
    private Boolean isHidden;
    private List<String> labels;
    private List<String> categories;
    private String timestamp;
    private String comment;

    public  Item item(){
        return new Item(this.itemId, this.isHidden, this.labels, this.categories, this.timestamp, this.comment);
    }
}
