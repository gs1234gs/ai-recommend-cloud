package com.guanshiyun;

import com.guanshiyun.embedding.ProductForEmbeddingApVO;
import com.guanshiyun.rpc.goodsapi.category.CategoryApiService;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.service.embedding.EmbeddingProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

@SpringBootTest
public class EmbeddingTest {
    @Autowired
    private ProductApiService productApiService;
    @Autowired
    private EmbeddingProductService embeddingProductService;
    @Autowired
    private CategoryApiService categoryApiService;
    @Autowired
    private TagApiService tagApiService;
    @Autowired
    private SkuApiService skuApiService;
//    @Test
//    void test1() {
//        productApiService.findCursor(
//                        RequestCursorPage.<ProductApiVO>builder().pageSize(100).build()
//                )
//                .flatMapMany(productPage -> {
//                    List<ProductApiVO> rows = productPage.getData().getRows();
//                    return Flux.fromIterable(rows)
//                            .flatMap(product -> {
//                                Mono<ResultT<List<CategoryApiVO>>> categoryMono = categoryApiService.findByProductId(product.getId());
//                                Mono<ResultT<List<TagApiVO>>> tagsMono = tagApiService.findByProductId(product.getId());
//                                Mono<ResultT<List<SKUApiVO>>> skuMono = skuApiService.findByProductId(product.getId());
//                                return Mono.zip(categoryMono, tagsMono, skuMono)
//                                        .map(tuple3 -> {
//                                            List<CategoryApiVO> category = tuple3.getT1().getData();
//                                            List<TagApiVO> tags = tuple3.getT2().getData();
//                                            List<SKUApiVO> sku = tuple3.getT3().getData();
//                                            return ProductForEmbeddingApVO
//                                                    .builder()
//                                                    .brand(product.getBrand())
//                                                    .id(product.getId())
//                                                    .title(product.getName())
//                                                    .skuList(
//                                                            sku.stream()
//                                                                    .map(skuC ->
//                                                                            ProductForEmbeddingApVO.SkuItem.builder()
//                                                                                    .id(skuC.getId())
//                                                                                    .name(skuC.getName())
//                                                                                    .skuCode(skuC.getSkuCode())
//                                                                                    .price(skuC.getPrice())
//                                                                                    .build()
//                                                                    )
//                                                                    .toList()
//                                                    )
//                                                    .tagNames(tags.stream().map(TagApiVO::getName).toList())
//                                                    .categoryNames(category.stream().map(CategoryApiVO::getName).toList())
//                                                    .placeOfOrigin(product.getPlaceOfOrigin())
//                                                    .build();
//                                        });
//                            });
//                })
//                .collectList() // 得到 List<ProductForEmbeddingApVO>
//                .flatMapMany(list -> embeddingProductService.saveBatch(list)) // ✅ 关键：用 flatMapMany 展开并执行 Flux
//                .doOnNext(saved -> System.out.println("Saved item: " + saved))
//                .doOnTerminate(() -> System.out.println("All embeddings saved!"))
//                .blockLast(Duration.ofSeconds(60)); // 阻塞直到最后一个元素完成
//    }
    @Test
    void test2() {
        embeddingProductService.recommendForUser(List.of(
                ProductForEmbeddingApVO.builder()
                        .id(BigInteger.ONE)
                        .title("vivo智能耳机 Pro + 无线降噪")
                        .build(),
                ProductForEmbeddingApVO.builder()
                        .id(BigInteger.TWO)
                        .title("Midea 美的一匹半 变频冷暖壁挂空调")
                        .build()
        ),5)
                .doOnNext(System.out::println)
                .block(Duration.ofSeconds(60));
    }

    public static void main(String[] args) {
        // 1. 创建一个 32 字节（256 位）的随机密钥
        byte[] secretKey = new byte[32];
        new SecureRandom().nextBytes(secretKey);

        // 2. 使用 Base64 编码
        String base64Token = Base64.getEncoder().encodeToString(secretKey);

        // 3. 输出结果（可直接复制到 Docker 环境变量中）
        System.out.println("✅ 生成的 NACOS_AUTH_TOKEN（已 Base64 编码，长度合规）:");
        System.out.println(base64Token);
        System.out.println("\n💡 使用示例（PowerShell）:");
    }

}
