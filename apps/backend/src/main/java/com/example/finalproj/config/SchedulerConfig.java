package com.example.finalproj.config;

import com.example.finalproj.domain.chat.context.service.ChatService;
import com.example.finalproj.ml.chatML.ChatMLService;
import com.example.finalproj.redis.service.RedisChatService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SchedulerConfig {

    private final RedisChatService redisChatService;

    public SchedulerConfig(RedisChatService redisChatService) {
        this.redisChatService = redisChatService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void scheduleMoveExpiredChatsToDb() {
        try {
            redisChatService.moveExpiredChatsToDb();
            System.out.println("자정마다 실행됨: Redis에서 만료된 채팅을 DB로 이동");
        } catch (Exception e) {
            System.err.println("에러 발생: " + e.getMessage());
        }
    }
}
