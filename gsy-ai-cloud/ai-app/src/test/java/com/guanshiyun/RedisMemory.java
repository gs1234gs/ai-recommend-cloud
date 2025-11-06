package com.guanshiyun;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
@SpringBootTest
public class RedisMemory implements ChatMemoryRepository {


    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    private final String PREFIX = "chat:"; // Redis key 前缀
    private final String CONVERSATION_SET = "chat:conversations"; // 保存所有会话ID

    @Override
    public List<String> findConversationIds() {
        ReactiveSetOperations<String, Object> setOps = redisTemplate.opsForSet();
        return Objects.requireNonNull(setOps.members(CONVERSATION_SET)
                .map(Object::toString)
                .collectList()
                .block());
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        ReactiveListOperations<String, Object> listOps = redisTemplate.opsForList();
        Flux<Object> objects = listOps.range(PREFIX + conversationId, 0, -1);
        return Objects.requireNonNull(objects
                .map(obj -> (Message) obj)
                .collectList()
                .block());
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        ReactiveListOperations<String, Object> listOps = redisTemplate.opsForList();
        // 清空旧消息
        redisTemplate.delete(PREFIX + conversationId);
        // 保存新消息
        messages.forEach(msg -> listOps.rightPush(PREFIX + conversationId, msg));

        // 保存会话ID到集合
        ReactiveSetOperations<String, Object> setOps = redisTemplate.opsForSet();
        setOps.add(CONVERSATION_SET, conversationId);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(PREFIX + conversationId);
        redisTemplate.opsForSet().remove(CONVERSATION_SET, conversationId);
    }
}
