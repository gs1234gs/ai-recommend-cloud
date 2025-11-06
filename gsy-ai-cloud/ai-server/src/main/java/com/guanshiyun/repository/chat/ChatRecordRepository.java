package com.guanshiyun.repository.chat;

import com.guanshiyun.chat.ChatRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface ChatRepository extends ReactiveCrudRepository<ChatRecord, BigInteger> {
}
