package com.example.finalproj.redis.service;

import com.example.finalproj.ml.chatML.ChatMLService;
import com.example.finalproj.domain.chat.context.entity.ChatMessageDTO;
import com.example.finalproj.domain.chat.context.repository.ChatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisChatService {
    private static final Logger logger = LoggerFactory.getLogger(RedisChatService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;

    public RedisChatService(RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper,
                            ChatRepository chatRepository) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.chatRepository = chatRepository;
    }

    public void saveMessage(ChatMessageDTO message) {
        String key = getChatKey(message.getUserId(), message.getBabyId());
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 7, TimeUnit.DAYS); // 7일 후 만료

        // ML 서비스로 메시지 전송
//        chatMLService.getResponse(message, message.getSessionId());
    }

    public List<ChatMessageDTO> getChatHistory(Long userId, Long babyId) {
        String key = getChatKey(userId, babyId);
        return redisTemplate.opsForList().range(key, 0, -1).stream()
                .map(msg -> objectMapper.convertValue(msg, ChatMessageDTO.class))
                .toList();
    }

    public void deleteChatHistory(Long userId, Long babyId) {
        String key = getChatKey(userId, babyId);
        redisTemplate.delete(key);
    }

    public void resetChatHistory(Long userId, Long babyId) {
        String key = getChatKey(userId, babyId);
        try {
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                logger.info("Chat history reset successfully for userId: {} and babyId: {}", userId, babyId);
            } else {
                logger.warn("No chat history found to reset for userId: {} and babyId: {}", userId, babyId);
            }
        } catch (Exception e) {
            logger.error("Error resetting chat history in Redis for userId: {} and babyId: {}", userId, babyId, e);
            throw new RuntimeException("Error resetting chat history in Redis", e);
        }
    }

    private String getChatKey(Long userId, Long babyId) {
        return "chat:" + userId + ":" + babyId;
    }

    // 이 메서드는 SchedulerConfig에서 호출
    public void moveExpiredChatsToDb() {
        try {
            // Redis에서 모든 키 가져오기
            List<String> keys = redisTemplate.keys("chat:*").stream().toList();

            for (String key : keys) {
                Long userId = extractUserId(key);
                Long babyId = extractBabyId(key);

                // Redis에서 채팅 기록 가져오기
                List<ChatMessageDTO> messages = getChatHistory(userId, babyId);

                // 메시지를 모두 DB로 옮기기
                messages.forEach(msg -> {
                    try {
                        // DB에 메시지 저장
                        chatRepository.save(msg);
                        System.out.println("Saved message to DB: " + msg);
                    } catch (Exception e) {
                        System.err.println("메시지 저장 오류: " + e.getMessage());
                    }
                });

                // DB로 성공적으로 옮긴 후 Redis에서 기록 삭제
                resetChatHistory(userId, babyId);
                System.out.println("Reset chat history for userId: " + userId + " and babyId: " + babyId);
            }
        } catch (Exception e) {
            System.err.println("Redis 조회 오류: " + e.getMessage());
        }
    }

    private Long extractUserId(String key) {
        return Long.parseLong(key.split(":")[1]);
    }

    private Long extractBabyId(String key) {
        return Long.parseLong(key.split(":")[2]);
    }
}