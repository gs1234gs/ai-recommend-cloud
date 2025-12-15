package com.guanshiyun.embedding;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum EmbeddingProductEnum {
    EMBEDDING_PRODUCT("商品向量表", "embedding_product");
    private String name;
    private String value;
    private static final String[] ARRAYS = Arrays.stream(values())
            .map(e -> e.value)
            .toArray(String[]::new);
}
