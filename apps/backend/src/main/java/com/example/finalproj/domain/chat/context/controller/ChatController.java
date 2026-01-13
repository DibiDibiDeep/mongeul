package com.example.finalproj.domain.chat.context.controller;

import com.example.finalproj.domain.chat.context.entity.ChatMessageDTO;
import com.example.finalproj.domain.chat.context.service.ChatService;
import com.example.finalproj.auth.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ChatController(ChatService chatService, JwtTokenProvider jwtTokenProvider) {
        this.chatService = chatService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatMessageDTO message, @RequestHeader(value = "Authorization") String authHeader, boolean resetHistory) {
        try {
            String authToken = authHeader.replace("Bearer ", "");
            if (!jwtTokenProvider.validateToken(authToken)) {
                logger.warn("Invalid token received: {}", authToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long tokenUserId = jwtTokenProvider.getUserIdFromToken(authToken);
            if (!tokenUserId.equals(message.getUserId())) {
                logger.warn("Token user ID does not match message user ID. Token user ID: {}, Message user ID: {}", tokenUserId, message.getUserId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            logger.info("Processing message for user ID: {} and baby ID: {}", message.getUserId(), message.getBabyId());
            ChatMessageDTO response = chatService.processMessage(message, authToken, resetHistory);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input received: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error processing message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history/{userId}/{babyId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable Long userId,
                                                               @PathVariable Long babyId,
                                                               @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid authorization header received");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                logger.warn("Invalid token received: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long tokenUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (tokenUserId == null || !tokenUserId.equals(userId)) {
                logger.warn("Token user ID does not match path user ID. Token user ID: {}, Path user ID: {}", tokenUserId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            logger.info("Fetching chat history for user ID: {} and baby ID: {}", userId, babyId);
            List<ChatMessageDTO> chatHistory = chatService.getChatHistory(userId, babyId);
            return ResponseEntity.ok(chatHistory);
        } catch (Exception e) {
            logger.error("Error fetching chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset/{userId}/{babyId}")
    public ResponseEntity<Void> resetChatHistory(@PathVariable Long userId,
                                                 @PathVariable Long babyId,
                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid authorization header received");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                logger.warn("Invalid token received: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long tokenUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (tokenUserId == null || !tokenUserId.equals(userId)) {
                logger.warn("Token user ID does not match path user ID. Token user ID: {}, Path user ID: {}", tokenUserId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            logger.info("Resetting chat history for user ID: {} and baby ID: {}", userId, babyId);
            chatService.resetChatHistory(userId, babyId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error resetting chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetChatHistory(
            @RequestBody ChatMessageDTO messageDTO,
            @RequestHeader ("Authorization") String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid authorization header received");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                logger.warn("Invalid token received: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long tokenUserId = jwtTokenProvider.getUserIdFromToken(token);
            if (tokenUserId == null || !tokenUserId.equals(messageDTO.getUserId())) {
//                logger.warn("Token user ID does not match path user ID. Token user ID: {}, Path user ID: {}", tokenUserId, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            chatService.resetChatHistoryML(messageDTO.getUserId(), messageDTO.getBabyId(), true);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error resetting chat history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
