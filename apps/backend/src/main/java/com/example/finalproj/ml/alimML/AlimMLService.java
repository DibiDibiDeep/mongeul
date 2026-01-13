package com.example.finalproj.ml.alimML;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AlimMLService {
    @Value("${ml.service.url}")
    private String mlServiceUrl;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public AlimMLService(RestTemplate restTemplate, ApplicationEventPublisher eventPublisher) {
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
    }

    public void sendAlimToMLService(Integer userId, Integer babyId, String content, Integer AlimId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = String.format("{\"user_id\": %d, \"baby_id\": %d, \"report\": \"%s\"}",
                userId, babyId, content);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        try {
            String mlResponse = restTemplate.postForObject(mlServiceUrl + "/generate_diary", request, String.class);
            System.out.println("ML Service response: " + mlResponse);
            eventPublisher.publishEvent(new AlimMLProcessingCompletedEvent(this, mlResponse, AlimId));
        } catch (Exception e) {
            System.err.println("Error in ML service process: " + e.getMessage());
        }
    }

}