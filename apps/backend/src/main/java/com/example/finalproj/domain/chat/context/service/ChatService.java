package com.example.finalproj.domain.chat.context.service;

import com.example.finalproj.domain.chat.context.entity.ChatMessageDTO;
import com.example.finalproj.domain.chat.context.repository.ChatRepository;
import com.example.finalproj.ml.chatML.ChatMLService;
import com.example.finalproj.redis.service.RedisChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private static final int MAX_CONTENT_LENGTH = 65535;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final ZoneId seoulZone = ZoneId.of("Asia/Seoul");

    private final ChatRepository chatRepository;
    private final ChatMLService chatMLService;
    private final RedisChatService redisChatService;

    @Autowired
    public ChatService(ChatRepository chatRepository, ChatMLService chatMLService, RedisChatService redisChatService) {
        this.chatRepository = chatRepository;
        this.chatMLService = chatMLService;
        this.redisChatService = redisChatService;
    }

    public ChatMessageDTO processMessage(ChatMessageDTO message, String authToken, boolean resetHistory) {
        // Validate input
        if (message.getBabyId() == null) {
            logger.error("Received message with null babyId: {}", message);
            throw new IllegalArgumentException("babyId cannot be null");
        }

        message.setRole("user");

        // Truncate content if it exceeds the maximum length
        if (message.getContent().length() > MAX_CONTENT_LENGTH) {
            logger.warn("Message content exceeds maximum length. Truncating...");
            message.setContent(message.getContent().substring(0, MAX_CONTENT_LENGTH));
        }

        try {
            // Set sessionId for the user message
            message.setSessionId(authToken);

            // Set current Seoul time
            message.setTimestamp(LocalDateTime.now(seoulZone));

            // Save the user message in redis
            redisChatService.saveMessage(message);
            logger.info("Saved user message to Redis: {}", message);

            // Process the message using ML service
            ChatMessageDTO response = chatMLService.getResponse(message, authToken, resetHistory);

            // Ensure babyId is set in the response
            if (response.getBabyId() == null) {
                logger.warn("ML service response had null babyId, setting to original message babyId");
                response.setBabyId(message.getBabyId());
            }

            // Truncate response content if it exceeds the maximum length
            if (response.getContent().length() > MAX_CONTENT_LENGTH) {
                logger.warn("Response content exceeds maximum length. Truncating...");
                response.setContent(response.getContent().substring(0, MAX_CONTENT_LENGTH));
            }

            // Save the bot response
            redisChatService.saveMessage(response);
            logger.info("Saved bot response to Redis: {}", response);

            response.setRole("bot");

            return response;
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
            throw new RuntimeException("Error processing chat message", e);
        }
    }

    public List<ChatMessageDTO> getChatHistory(Long userId, Long babyId) {
        logger.info("Fetching chat history for userId: {} and babyId: {}", userId, babyId);
        return redisChatService.getChatHistory(userId, babyId);
    }

    public void resetChatHistory(Long userId, Long babyId) {
        redisChatService.resetChatHistory(userId, babyId);
    }

    public void resetChatHistoryML(Long userId, Long babyId, boolean resetHistory) {
        // ML 서비스로 요청 보내기
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("user_id", userId);
        requestData.put("baby_id", babyId);
        requestData.put("reset_history", resetHistory);

        chatMLService.resetChatHistoryML(userId, babyId, resetHistory);

    }
}
