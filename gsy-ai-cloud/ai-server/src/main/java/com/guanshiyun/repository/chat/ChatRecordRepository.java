package com.guanshiyun.repository.chat;

import com.guanshiyun.chat.ChatRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;



public interface ChatRecordRepository extends ReactiveCrudRepository<ChatRecord, Long> {
}
