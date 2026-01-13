package com.example.finalproj.ml.chatML;

import com.example.finalproj.domain.chat.context.repository.ChatRepository;
import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.domain.chat.context.entity.ChatMessageDTO;
import com.example.finalproj.domain.memo.original.entity.Memo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMLService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMLService.class);

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;

    public ChatMLService(RestTemplate restTemplate, ObjectMapper objectMapper, ChatRepository chatRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.chatRepository = chatRepository;
    }

    public void sendAlimInfToMLService(AlimInf alimInf) {
        Map<String, Object> requestData = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();

        item.put("user_id", alimInf.getUserId());
        item.put("baby_id", alimInf.getBabyId());
        item.put("date", alimInf.getDate().toLocalDate().toString());
        item.put("role", "child");
        item.put("text", alimInf.getDiary());

        items.add(item);
        requestData.put("items", items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        try {
            restTemplate.postForObject(mlServiceUrl + "/embedding", request, String.class);
        } catch (Exception e) {
            // 에러 처리
            e.printStackTrace();
            // 여기서 로깅을 추가하거나 다른 에러 처리 로직을 구현할 수 있습니다.
        }
    }

    public void sendMemoToMLService(Memo memo) {
        Map<String, Object> requestData = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();

        LocalDateTime memoDateTime = memo.getDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        item.put("user_id", memo.getUserId());
        item.put("baby_id", memo.getTodayId()); // TodayId를 baby_id로 사용
        item.put("date", memoDateTime.format(dateFormatter));
        item.put("role", "parents");

        // text 내용 앞에 시분초 추가
        String textWithTime = memoDateTime.format(timeFormatter) + " " + memo.getContent();
        item.put("text", textWithTime);

        // sendToML 필드는 제외

        items.add(item);
        requestData.put("items", items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        try {
            restTemplate.postForObject(mlServiceUrl + "/embedding", request, String.class);
        } catch (Exception e) {
            // 에러 처리
            e.printStackTrace();
            // 여기서 로깅을 추가하거나 다른 에러 처리 로직을 구현할 수 있습니다.
        }
    }

    public ChatMessageDTO getResponse(ChatMessageDTO message, String authToken, boolean resetHistory) {
        String previousResponse = getPreviousResponse(message.getUserId(), message.getBabyId());

        if (isInvalidInput(message.getContent(), previousResponse)) {
            return createErrorResponse("잘못된 입력입니다.", message);
        }

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("user_id", message.getUserId());
        requestData.put("baby_id", message.getBabyId());
        requestData.put("text", message.getContent());
        requestData.put("timestamp", message.getTimestamp());
        requestData.put("role", message.getRole());
        requestData.put("session_id", authToken);
        requestData.put("reset_history", resetHistory );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        try {
            String endpoint = mlServiceUrl + "/process_query";
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpoint, request, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String response = jsonNode.get("response").asText();

                ChatMessageDTO botResponse = new ChatMessageDTO(
                        message.getUserId(),
                        message.getBabyId(),
                        message.getTimestamp(),
                        response,
                        "bot",
                        "bot",
                        authToken
                );

                if (!resetHistory) {
                    saveResponse(botResponse);
                }
                return botResponse;
            } else {
                throw new RuntimeException("ML service returned non-200 status: " + responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            return createErrorResponse("ML 서비스와 통신 중 오류가 발생했습니다.", message);
        }
    }

    private String getPreviousResponse(Long userId, Long babyId) {
        List<ChatMessageDTO> recentMessages = chatRepository.findRecentMessagesByUserAndBaby(
                userId, babyId, PageRequest.of(0, 1));
        return recentMessages.isEmpty() ? null : recentMessages.get(0).getContent();
    }

    private boolean isInvalidInput(String input, String previousResponse) {
        return input.equals(previousResponse) || input.isEmpty();
    }

    private ChatMessageDTO createErrorResponse(String errorMessage, ChatMessageDTO originalMessage) {
        return new ChatMessageDTO(
                originalMessage.getUserId(),
                originalMessage.getBabyId(),
                originalMessage.getTimestamp(),
                errorMessage,
                "bot",
                "error",
                originalMessage.getSessionId()
        );
    }

    private void saveResponse(ChatMessageDTO response) {
        chatRepository.save(response);
    }

    public void resetChatHistoryML(Long userId, Long babyId, boolean resetHistory) {
        logger.info("Resetting chat history for user ID: {}, baby ID: {}, resetHistory: {}", userId, babyId, resetHistory);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("user_id", userId);
        requestData.put("baby_id", babyId);
        requestData.put("reset_history", resetHistory);
        requestData.put("text", ""); // ML 서비스가 텍스트 필드를 요구할 경우 빈 문자열 전송

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        try {
            String endpoint = mlServiceUrl + "/process_query" + (resetHistory ? "?reset_history=true" : "");
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpoint, request, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully reset chat history for user ID: {} and baby ID: {}", userId, babyId);
            } else {
                logger.error("Failed to reset chat history. ML service returned non-200 status: {}", responseEntity.getStatusCodeValue());
                throw new RuntimeException("ML service returned non-200 status: " + responseEntity.getStatusCodeValue());
            }
        } catch (RestClientException e) {
            logger.error("Error communicating with ML service while resetting chat history", e);
            throw new RuntimeException("Error communicating with ML service", e);
        } catch (Exception e) {
            logger.error("Unexpected error while resetting chat history", e);
            throw new RuntimeException("Unexpected error while resetting chat history", e);
        }
    }
}
