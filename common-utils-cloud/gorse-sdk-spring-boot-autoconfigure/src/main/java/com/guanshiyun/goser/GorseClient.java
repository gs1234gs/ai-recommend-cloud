package com.guanshiyun.goser;

import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.goconfig.GorseProperties;
import com.guanshiyun.items.Item;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.user.User;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class GorseClient implements Serializable {
    private GorseProperties gorseProperties;
    @Serial
    private static final long serialVersionUID = 1L;
    private String url;
    private String apiKey;

    public Mono<RowAffected> saveUser(User user) {
        return request(HttpMethod.POST,
                urlBuffer().append("/api/user").toString(),
                user,
                RowAffected.class
        );
    }


    public Mono<User> findUser(String userId) {
        return request(HttpMethod.GET,
                urlBuffer().append("/api/user/").append(userId).toString(),
                null,
                User.class
        );
    }

    public Mono<RowAffected> deleteUser(String userId) {
        return this.request(HttpMethod.DELETE,
                urlBuffer().append("/api/user/").append(userId).toString(),
                null,
                RowAffected.class
        );
    }

    public Mono<RowAffected> saveItem(Item item) {
        return this.request(HttpMethod.POST,
                urlBuffer().append("/api/item").toString(),
                item,
                RowAffected.class
        );
    }

    public Mono<Item> getItem(String itemId) {
        return this.request(HttpMethod.GET,
                urlBuffer().append("/api/item/").append(itemId).toString(),
                null,
                Item.class
        );
    }

    public Mono<RowAffected> deleteItem(String itemId) {
        return this.request(HttpMethod.DELETE,
                urlBuffer().append("/api/item/").append(itemId).toString(),
                null,
                RowAffected.class
        );
    }

    public Mono<RowAffected> insertFeedback(List<Feedback> feedbacks) {
        return this.request(HttpMethod.POST,
                urlBuffer().append("/api/feedback").toString(),
                feedbacks,
                RowAffected.class
        );
    }
    public Mono<RowAffected> deleteFeedback(String feedbackType, String userId, String itemId) throws IOException {
        return this.request(HttpMethod.DELETE,
                urlBuffer().append("/api/feedback/").append(feedbackType).append("/").append(userId).append("/").append(itemId).toString(),
                null, RowAffected.class
        );
    }
//
    public Mono<List<Feedback>> listFeedback(String userId, String feedbackType) {
        return this.request(HttpMethod.GET,
                urlBuffer().append("/api/user/").append(userId).append("/feedback/").append(feedbackType).toString(),
                        null,
                        Feedback[].class
                )
                .map(Arrays::asList);
    }

    public Mono<List<String>> getRecommend(String userId, int n) {
        return webClient()
                .get()
                .uri(builder -> builder.path("/api/recommend/{userId}")
                        .queryParam("n", n)
                        .build(userId))
                .retrieve()
                .bodyToMono(String[].class)
                .map(Arrays::asList);
    }
    public Mono<List<String>> getRecommend(String userId) {
        return this.request(HttpMethod.GET,
                        urlBuffer().append("/api/recommend/").append(userId).toString(),
                        null,
                        String[].class
                )
                .map(Arrays::asList);
    }

    public <Req, Res> Mono<Res> request(HttpMethod method,
                                        String url,
                                        Req request,
                                        Class<Res> responseType) throws NullPointerException {
        WebClient webClient = webClient();
        if (Objects.isNull(request)) {
            return webClient
                    .method(method)
                    .uri(url)
                    .retrieve()
                    .bodyToMono(responseType);
        }
        return webClient
                .method(method)
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType);

    }

    private WebClient webClient() {
        return WebClient.builder()
                .defaultHeaders(headers -> {
                    headers.add("X-API-Key", this.apiKey);
                    headers.add("Content-Type", "application/json");
                }).build();
    }

    private StringBuffer urlBuffer(){
        return new StringBuffer(this.url);
    }

}
