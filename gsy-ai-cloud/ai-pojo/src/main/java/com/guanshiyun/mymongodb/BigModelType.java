package com.guanshiyun.mymongodb;

import com.guanshiyun.json.KeyValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Document(collection = "big_model_type")
public class BigModelType{
    private Long id;
    private List<KeyValue> keyValueList;
}
