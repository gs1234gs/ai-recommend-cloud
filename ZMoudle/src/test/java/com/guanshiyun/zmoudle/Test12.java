package com.guanshiyun.zmoudle;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
public class Test12 {
    @Autowired
    ProductMoRepository productMoRepository;
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Test
    public void removeClassField() {
        Query query = new Query();
        Update update = new Update().unset("_class");
        // 批量更新，将所有数据的 _class 字段移除
        reactiveMongoTemplate.updateMulti(query, update, Item.class).block();
        System.out.println("历史数据的 _class 字段已清理完毕！");
    }
    @Test
    public void test2() {
        // 1. 修改点：添加 .withLocale(Locale.ENGLISH)
        // 这是解决 "index 0" 报错的关键
        DateTimeFormatter inputFormatter = DateTimeFormatter
                .ofPattern("EEE MMM dd HH:mm:ss Z yyyy")
                .withLocale(java.util.Locale.ENGLISH); // 强制使用英语解析 Thu, Feb

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        List<Item> topics1 = productMoRepository.saveAll(
                productMoRepository.findAll()
                        .map(item -> {
                            try {
                                String rawTimestamp = item.getTimestamp();

                                // 逻辑保持不变，但建议加个判空
                                if (rawTimestamp == null) return item;

                                // 如果原始数据里直接就是 +0800，就不需要 replace 了
                                // 如果数据里还是 CST，这行代码依然保留作为双重保险
                                String fixedTimestamp = rawTimestamp.replace("CST", "+0800");

                                LocalDateTime localDateTime = LocalDateTime.parse(fixedTimestamp, inputFormatter);
                                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
                                String gorseTimestamp = zonedDateTime.format(outputFormatter);
                                item.setTimestamp(gorseTimestamp);

                            } catch (Exception e) {
                                System.err.println("解析失败: " + item.getTimestamp() + " | 错误: " + e.getMessage());
                            }
                            return item;
                        })
        ).collectList().block();

        System.out.println("=================");
        System.out.println(topics1);
    }



}

interface ProductMoRepository extends ReactiveMongoRepository<Item, String> {

}

@Data
@Document(collection = "items")
class Item{

    private String id;


    @Field("itemid")  // 映射到MongoDB中的itemid字段
    private String itemId;

    @Field("ishidden")
    private Boolean isHidden;

    @Field("labels")
    private Object labels;  // 或者具体类型如 List<String> 或 Map<String, Object>

    @Field("categories")
    private List<String> categories;

    @Field("timestamp")
    private String timestamp;

    @Field("comment")
    private String comment;
}
