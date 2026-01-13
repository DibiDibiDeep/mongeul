package com.example.finalproj.ml.voiceML;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class VoiceMLService {

    @Value("${ml.service.url}")
    private String mlServiceUrl;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public VoiceMLService(RestTemplate restTemplate, ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    public void sendVoiceToMLService(MultipartFile file, Integer userId, Integer babyId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("user_id", userId);
        body.add("baby_id", babyId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    mlServiceUrl + "/whisper",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            String mlResponse = response.getBody();
            System.out.println("ML Service response: " + mlResponse);

            // Parse JSON and extract transcription
            JsonNode rootNode = objectMapper.readTree(mlResponse);
            String transcription = rootNode.path("transcription").asText();

            // Publish the event with the transcription
            eventPublisher.publishEvent(new VoiceMLProcessingCompletedEvent(this, transcription, userId, babyId));
        } catch (Exception e) {
            System.err.println("Error in ML service process: " + e.getMessage());
            throw e;
        }
    }
}