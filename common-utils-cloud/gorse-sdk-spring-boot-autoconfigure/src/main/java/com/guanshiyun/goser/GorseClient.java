package com.guanshiyun.goser;

import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.items.Item;
import com.guanshiyun.rowAffected.RowAffected;
import com.guanshiyun.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
@Slf4j
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class GorseClient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String url;
    private String apiKey;

    public Mono<RowAffected> saveUser(User user) {
        return request(HttpMethod.POST,
                "/api/user",
                user,
                RowAffected.class
        );
    }


    public Mono<User> findUser(String userId) {
        return request(HttpMethod.GET,
                "/api/user/" + userId,
                null,
                User.class
        );
    }

    public Mono<RowAffected> deleteUser(String userId) {
        return this.request(HttpMethod.DELETE,
                "/api/user/" + userId,
                null,
                RowAffected.class
        );
    }

    public Mono<RowAffected> saveItem(Item item) {
        return this.request(HttpMethod.POST,
                "/api/item",
                item,
                RowAffected.class
        );
    }

    public Mono<Item> getItem(String itemId) {
        return this.request(HttpMethod.GET,
                "/api/item/" + itemId,
                null,
                Item.class
        );
    }

    public Mono<RowAffected> deleteItem(String itemId) {
        return this.request(HttpMethod.DELETE,
                "/api/item/" + itemId,
                null,
                RowAffected.class
        );
    }

    public Mono<RowAffected> insertFeedback(List<Feedback> feedbacks) {
        return this.request(HttpMethod.POST,
                "/api/feedback",
                feedbacks,
                RowAffected.class
        );
    }
    public Mono<RowAffected> deleteFeedback(String feedbackType, String userId, String itemId) throws IOException {
        return this.request(HttpMethod.DELETE,
                "/api/feedback/" + feedbackType + "/" + userId + "/" + itemId,
                null, RowAffected.class
        );
    }
//
    public Mono<List<Feedback>> listFeedback(String userId, String feedbackType) {
        return this.request(HttpMethod.GET,
                        "/api/user/" + userId + "/feedback/" + feedbackType,
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
                .map(Arrays::asList)
                .map(this::filterValidNumericIds);
    }
    public Mono<List<String>> getRecommend(String userId) {
        return this.request(HttpMethod.GET,
                        "/api/recommend/" + userId,
                        null,
                        String[].class
                )
                .map(Arrays::asList)
                .map(this::filterValidNumericIds);
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
                .baseUrl(this.url)
                .defaultHeaders(headers -> {
                    headers.add("X-API-Key", this.apiKey);
                    headers.add("Content-Type", "application/json");
                }).build();
    }
    /**
     * 只保留纯数字字符串（正整数），并记录非法 ID 用于排查
     */
    private List<String> filterValidNumericIds(List<String> rawIds) {
        if (rawIds == null || rawIds.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("Raw IDs: {}", rawIds);
        List<String> validIds = new ArrayList<>();
        List<String> invalidIds = new ArrayList<>();

        for (String id : rawIds) {
            if (id != null && id.matches("\\d+")) { // 仅匹配纯数字（不含符号、空格）
                validIds.add(id);
            } else {
                invalidIds.add(id);
            }
        }

        // 如果有非法 ID，记录警告（便于追踪 Gorse 脏数据）
        if (!invalidIds.isEmpty()) {
            log.warn("Gorse returned invalid non-numeric item IDs (ignored): {}", invalidIds);
        }

        return validIds;
    }
}
