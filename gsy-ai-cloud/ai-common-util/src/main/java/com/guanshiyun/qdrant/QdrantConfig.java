//package com.guanshiyun.qdrant;
//
//import com.guanshiyun.embedding.EmbeddingProductEnum;
//import io.qdrant.client.QdrantClient;
//import io.qdrant.client.QdrantGrpcClient;
//import io.qdrant.client.grpc.Collections;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//@RequiredArgsConstructor
//public class QdrantConfig {
//    //-------Qdrant---------
//    @Value("${spring.qdrant.rest-api}")
//    private String restApi;
//    @Value("${spring.qdrant.grpc-api}")
//    private String grpcApi;
//    @Value("${spring.qdrant.api-key}")
//    private String apiKey;
//
//    private final EmbeddingModel embeddingModel;
//
//    @Bean
//    @Qualifier("qdrantWebClient")
//    public WebClient qdrantWebClient() {
//        return WebClient.builder()
//                .baseUrl(restApi)
////                .defaultHeader("api-key", apiKey)
//                .build();
//    }
//
//    @SneakyThrows
//    @Bean
//    public QdrantClient qdrantClient() {
//        float[] embed = embeddingModel.embed("length");
//        QdrantClient qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(grpcApi).build());
//        qdrantClient.createCollectionAsync(EmbeddingProductEnum.EMBEDDING_PRODUCT.getValue(),
//                Collections.VectorParams.newBuilder().setDistance(Collections.Distance.Dot).setSize(embed.length).build()).get();
//        return qdrantClient;
//    }
//}
