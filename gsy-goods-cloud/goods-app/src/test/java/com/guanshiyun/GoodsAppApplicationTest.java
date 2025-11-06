package com.guanshiyun;


import com.db.dbnumber.ConstNumber;
import com.guanshiyun.controller.product.vo.ProductSaveVO;
import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.product.ProductService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
@Slf4j
@SpringBootTest
public class GoodsAppApplicationTest
{

    /**
     *测试对象
     * */
    @Autowired
    private ProductService productService;
    @Test
    public void test()
    {

        productService.save(build())
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .onErrorResume(throwable -> {
                    log.error("保存失败：",throwable);
                    return Mono.just(ConstNumber.BIG_INTEGER_ZERO);
                })
                .subscribe(i->{
                    log.info("保存成功：{}",i);
                });
    }


    @Test
    public void test2()
    {
        productService.findPage(

                RequestPage.<ProductVO>builder()
                        .pageNum(BigInteger.valueOf(1))
                        .pageSize(10)
                        .build()

        ).flatMap(pageResultT -> {
            log.info("查询结果：{}",pageResultT);
            return Mono.just(pageResultT);
        })
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .onErrorResume(throwable -> {
                    log.error("分页查询失败：",throwable);
                    return Mono.just(PageResultT.<List<ProductVO>>builder().build());
                })
                .subscribe(i->{
            log.info("查询结果：{}",i);
        });
    }
    @Test
    public void test3()
    {
        productService.deleteById(BigInteger.valueOf(1))
                .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
                .onErrorResume(throwable -> {
                    log.error("删除失败：",throwable);
                    return Mono.just(ConstNumber.LONG_ZERO);
                })
                .subscribe(i->{
                    log.info("删除成功：{}",i);
                });
    }

    @Test
    public void test4(){
        productService.findCursor(
                RequestCursorPage.<ProductVO>builder()
                        .lastId(BigInteger.valueOf(2))
                        .pageSize(10)
                        .build()
        ).flatMap(item->{
            log.info("查询结果：{}",item);
            return Mono.just(item);
        })
                .subscribe();
    }


@Test
public void test5(){
    productService.save(generateDiverseTestProducts())
            .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, BigInteger.valueOf(1)))
            .onErrorResume(throwable -> {
                log.error("批量保存失败：",throwable);
                return Mono.just(ConstNumber.LONG_ZERO);
            })
            .subscribe(i->{
                log.info("批量保存成功：{}",i);
            });
}












    public static ProductSaveVO build(){
        return ProductSaveVO.builder()
//                .id(BigInteger.valueOf(1001))
                .name("vivo智能耳机 Pro + 无线降噪")
                .price(new BigDecimal("299.00"))
                .description("高保真音质，主动降噪，超长续航72小时，支持无线充电。")
                .image("https://example.com/images/earphones-pro.jpg")
                .video("https://example.com/videos/earphones-pro.mp4")
                .brand("SoundMax")
                .placeOfOrigin("中国上海")
                .level((short) 1)  // 假设 1=一级品
                .stock(150)
                .salesVolume(86)
                .status((short) 1)  // 1=上架
                .publishTime(LocalDateTime.of(2025, 3, 15, 10, 0))  // 上架时间
                .offlineTime(LocalDateTime.of(2026, 3, 15, 10, 0))  // 预计下架时间
                .warehouseId(List.of(
                        big(1), big(2), big(3)
                ))
                .categoryId(BigInteger.valueOf(1))
                .tagId(BigInteger.valueOf(1))
                .build();
    }
    public static List<ProductSaveVO> generateDiverseTestProducts() {
        List<ProductSaveVO> products = new ArrayList<>();

        // 1. Apple iPhone 16 Pro Max
        products.add(ProductSaveVO.builder()
                .name("Apple iPhone 16 Pro Max 256GB 钛金属原色")
                .price(new BigDecimal("9999.00"))
                .description("A17 Pro芯片，5倍光学变焦，灵动岛设计，支持Apple Intelligence。")
                .image("https://example.com/images/iphone16promax.jpg")
                .video("https://example.com/videos/iphone16promax.mp4")
                .brand("Apple")
                .placeOfOrigin("中国郑州")
                .level((short) 1)
                .stock(80)
                .salesVolume(120)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 9, 20, 10, 0))
                .offlineTime(LocalDateTime.of(2026, 9, 20, 10, 0))
                .warehouseId(

                        List.of(big(2), big(4), big(5))
                )
                .categoryId(BigInteger.valueOf(1001)) // 手机
                .tagId(BigInteger.valueOf(8001))
                .build());

        // 2. Samsung Galaxy Z Fold6
        products.add(ProductSaveVO.builder()
                .name("Samsung Galaxy Z Fold6 512GB 星夜黑")
                .price(new BigDecimal("15999.00"))
                .description("7.6英寸主屏，IPX8防水，S Pen支持，超薄折叠设计。")
                .image("https://example.com/images/fold6.jpg")
                .video("https://example.com/videos/fold6.mp4")
                .brand("Samsung")
                .placeOfOrigin("韩国")
                .level((short) 1)
                .stock(40)
                .salesVolume(33)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 7, 15, 11, 0))
                .offlineTime(LocalDateTime.of(2026, 7, 15, 11, 0))
                .warehouseId(
                        List.of(big(4), big(6), big(9))
                )
                .categoryId(BigInteger.valueOf(1001))
                .tagId(BigInteger.valueOf(8002))
                .build());

        // 3. Huawei MatePad Pro 13
        products.add(ProductSaveVO.builder()
                .name("Huawei MatePad Pro 13英寸 12GB+512GB")
                .price(new BigDecimal("4299.00"))
                .description("鸿蒙系统，星闪手写笔，多设备协同，生产力平板。")
                .image("https://example.com/images/matepad-pro.jpg")
                .video("https://example.com/videos/matepad-pro.mp4")
                .brand("Huawei")
                .placeOfOrigin("中国东莞")
                .level((short) 1)
                .stock(120)
                .salesVolume(78)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 4, 10, 9, 0))
                .offlineTime(LocalDateTime.of(2026, 4, 10, 9, 0))
                .warehouseId(
                        List.of(big(2))
                )
                .categoryId(BigInteger.valueOf(1002)) // 平板
                .tagId(BigInteger.valueOf(8003))
                .build());

        // 4. Xiaomi 14 Ultra 手机
        products.add(ProductSaveVO.builder()
                .name("Xiaomi 14 Ultra 16GB+1TB 陶瓷白")
                .price(new BigDecimal("6999.00"))
                .description("徕卡四摄，骁龙8 Gen3，2K超视感屏，澎湃P2快充芯片。")
                .image("https://example.com/images/xiaomi14ultra.jpg")
                .video("https://example.com/videos/xiaomi14ultra.mp4")
                .brand("Xiaomi")
                .placeOfOrigin("中国北京")
                .level((short) 1)
                .stock(100)
                .salesVolume(156)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 2, 28, 10, 0))
                .offlineTime(LocalDateTime.of(2026, 2, 28, 10, 0))
                .warehouseId(
                        List.of(big(11), big(13))
                )
                .categoryId(BigInteger.valueOf(1001))
                .tagId(BigInteger.valueOf(8004))
                .build());

        // 5. Dell XPS 13 笔记本
        products.add(ProductSaveVO.builder()
                .name("Dell XPS 13 9340 13.4英寸 超轻薄本")
                .price(new BigDecimal("8499.00"))
                .description("Intel Ultra 7，16GB内存，512GB SSD，InfinityEdge全面屏。")
                .image("https://example.com/images/dell-xps13.jpg")
                .video("https://example.com/videos/dell-xps13.mp4")
                .brand("Dell")
                .placeOfOrigin("美国")
                .level((short) 1)
                .stock(60)
                .salesVolume(45)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 5, 5, 14, 0))
                .offlineTime(LocalDateTime.of(2026, 5, 5, 14, 0))
                .warehouseId(
                        List.of(big(12))
                )
                .categoryId(BigInteger.valueOf(1003)) // 笔记本
                .tagId(BigInteger.valueOf(8005))
                .build());

        // 6. HP 暗影精灵10 游戏本
        products.add(ProductSaveVO.builder()
                .name("HP 暗影精灵10 16.1英寸 游戏笔记本")
                .price(new BigDecimal("7299.00"))
                .description("i7-14650HX + RTX 4060，165Hz高刷屏，RGB背光键盘。")
                .image("https://example.com/images/hp-omen10.jpg")
                .video("https://example.com/videos/hp-omen10.mp4")
                .brand("HP")
                .placeOfOrigin("中国重庆")
                .level((short) 1)
                .stock(70)
                .salesVolume(89)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 3, 12, 13, 0))
                .offlineTime(LocalDateTime.of(2026, 3, 12, 13, 0))
                .warehouseId(List.of(big(12)))
                .categoryId(BigInteger.valueOf(1003))
                .tagId(BigInteger.valueOf(8006))
                .build());

        // 7. Sony PS5 游戏主机
        products.add(ProductSaveVO.builder()
                .name("Sony PS5 数字版 游戏主机")
                .price(new BigDecimal("3499.00"))
                .description("定制SSD，支持4K 120Hz，DualSense手柄，沉浸式体验。")
                .image("https://example.com/images/ps5-digital.jpg")
                .video("https://example.com/videos/ps5.mp4")
                .brand("Sony")
                .placeOfOrigin("日本")
                .level((short) 1)
                .stock(50)
                .salesVolume(67)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 1, 18, 10, 0))
                .offlineTime(LocalDateTime.of(2026, 1, 18, 10, 0))
                .warehouseId(List.of(big(18)))
                .categoryId(BigInteger.valueOf(1004)) // 游戏设备
                .tagId(BigInteger.valueOf(8007))
                .build());

        // 8. Midea 1.5匹 变频空调
        products.add(ProductSaveVO.builder()
                .name("Midea 美的一匹半 变频冷暖壁挂空调")
                .price(new BigDecimal("2899.00"))
                .description("一级能效，自清洁，静音设计，APP远程控制。")
                .image("https://example.com/images/midea-ac.jpg")
                .video("https://example.com/videos/midea-ac.mp4")
                .brand("Midea")
                .placeOfOrigin("中国佛山")
                .level((short) 1)
                .stock(200)
                .salesVolume(312)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 4, 1, 9, 0))
                .offlineTime(LocalDateTime.of(2025, 10, 31, 9, 0)) // 季节性商品
                .warehouseId(List.of(big(16)))
                .categoryId(BigInteger.valueOf(1005)) // 家电
                .tagId(BigInteger.valueOf(8008))
                .build());

        // 9. Roborock 扫地机器人 S8
        products.add(ProductSaveVO.builder()
                .name("Roborock S8 Pro Ultra 扫拖一体机器人")
                .price(new BigDecimal("4599.00"))
                .description("AI避障，自动集尘，热水洗拖布，激光导航。")
                .image("https://example.com/images/roborock-s8.jpg")
                .video("https://example.com/videos/roborock-s8.mp4")
                .brand("Roborock")
                .placeOfOrigin("中国苏州")
                .level((short) 1)
                .stock(90)
                .salesVolume(73)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 6, 1, 10, 0))
                .offlineTime(LocalDateTime.of(2026, 6, 1, 10, 0))
                .warehouseId(List.of(big(12)))
                .categoryId(BigInteger.valueOf(1005))
                .tagId(BigInteger.valueOf(8009))
                .build());

        // 10. Nike Air Zoom Pegasus 40 跑鞋
        products.add(ProductSaveVO.builder()
                .name("Nike Air Zoom Pegasus 40 男子跑步鞋")
                .price(new BigDecimal("899.00"))
                .description("缓震回弹，透气网面，适合日常跑步与训练。")
                .image("https://example.com/images/nike-pegasus40.jpg")
                .video("https://example.com/videos/nike-pegasus.mp4")
                .brand("Nike")
                .placeOfOrigin("越南")
                .level((short) 1)
                .stock(500)
                .salesVolume(420)
                .status((short) 1)
                .publishTime(LocalDateTime.of(2025, 3, 1, 8, 0))
                .offlineTime(LocalDateTime.of(2026, 3, 1, 8, 0))
                .warehouseId(List.of(big(12)))
                .categoryId(BigInteger.valueOf(1006)) // 运动鞋
                .tagId(BigInteger.valueOf(8010))
                .build());

        return products;
    }

    public static BigInteger big(Integer  i){
        return BigInteger.valueOf(i);
    }
}
