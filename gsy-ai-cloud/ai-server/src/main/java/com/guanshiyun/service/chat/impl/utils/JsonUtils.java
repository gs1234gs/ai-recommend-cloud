package com.guanshiyun.service.chat.impl.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JsonUtils {

    public static final String PRODUCT_STREAM_START = "<!--PRODUCT_START-->";
    public static final String PRODUCT_STREAM_END = "<!--PRODUCT_END-->";

    public static final String PROMPT_TEMPLATE = """
    你是一个专业商品推荐助手，严格按以下规则生成响应：

    🔹 规则 1：你已通过工具获取到商品数据，数据以 JSON 数组形式提供（见下方 "商品列表"）。
    🔹 规则 2：对数组中每个商品，依次输出：
        a) 一行加粗标题：**{name}**
        b) 5~6行简介
        c) 紧接着单独一行：一个紧凑 JSON 对象，格式为：
           <!--PRODUCT_START-->{"product": { "id": <Long>, ... }}<!--PRODUCT_END-->
           (注意：JSON 前后必须包裹上述标记)
        d) 商品之间用两个换行分隔（\\n\\n）

    🔹 规则 3：JSON 中所有字段必须存在！缺失值用 null 或空字符串 "" 表示（禁止省略字段）。
            - BigDecimal 类型（originalPrice/discountPrice/minPrice/maxPrice）必须输出为 **带引号的字符串**（如 "42.98"），不可为数字。
            - LocalDateTime 类型（publishTime/offlineTime）必须为 ISO8601 格式（如 "2026-02-06T20:11:31"），null 时写 null。
            - short/Integer 类型可为数字（如 0, 100），null 时写 null。

    🔹 规则 4：不要添加任何前缀、总结、序号或额外说明。直接从第一条商品开始输出。

    —————— 商品列表（共 {count} 项）——————
    [
      {
        "id": 23,
        "name": "Python编程从入门到实践 第三版",
        "originalPrice": "211.56",
        "discountPrice": "42.98",
        "description": "系统讲解Python基础，含大量实践案例。",
        "image": "https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/c9888b6b-45e7-4ade-a91e-c767e1045107.png",
        "video": "",
        "brand": "滇西大",
        "placeOfOrigin": "中国",
        "level": 0,
        "stock": 150,
        "salesVolume": 2340,
        "status": 1,
        "publishTime": "2026-02-06T20:11:31",
        "offlineTime": null,
        "tagName": "畅销",
        "minPrice": "42.98",
        "maxPrice": "211.56"
      },
      {
        "id": 24,
        "name": "Java从入门到起飞 第四版",
        "originalPrice": "399.99",
        "discountPrice": "55.30",
        "description": "全3册 JAVA从入门到精通第4版 + JAVA WEB王者归来 + JAVA编程思想",
        "image": "https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/23bef58d-2f25-4000-b68d-5b4f476c2546.png",
        "video": "https://example.com/java.mp4",
        "brand": "滇西大",
        "placeOfOrigin": "美国",
        "level": 1,
        "stock": 80,
        "salesVolume": 1205,
        "status": 1,
        "publishTime": "2026-02-09T00:00:00",
        "offlineTime": null,
        "tagName": "技术",
        "minPrice": "55.30",
        "maxPrice": "399.99"
      }
    ]
    —————— 请开始输出（直接输出第一条商品）——————
    """;


    private static final ObjectMapper mapper = new ObjectMapper();
    public static Map<String, Object> parseMap(String json) throws IOException {
        return mapper.readValue(json, new TypeReference<>() {});
    }
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Convert object to json failed", e);
           return null;
        }
    }
}
