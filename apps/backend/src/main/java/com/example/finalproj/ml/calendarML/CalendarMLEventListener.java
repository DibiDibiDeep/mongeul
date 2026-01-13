package com.example.finalproj.ml.calendarML;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CalendarMLEventListener {

    private final RestTemplate restTemplate;
    private final String appUrl;

    // 생성자
    public CalendarMLEventListener(RestTemplate restTemplate, @Value("${ml.app.url}") String appUrl) {
        this.restTemplate = restTemplate;
        this.appUrl = appUrl;
    }

    // ML 처리 완료 이벤트 핸들러
    @EventListener
    public void handleMLProcessingCompletedEvent(CalendarMLProcessingCompletedEvent event) {
        String mlResponse = event.getMlResponse();
        Integer calendarPhotoId = event.getCalendarPhotoId();

        String forwardUrl = appUrl + "/api/calendar-photo-inf/" + calendarPhotoId.toString();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(mlResponse, headers);

            ResponseEntity<String> forwardResponse = restTemplate.postForEntity(forwardUrl, request, String.class);
            System.out.println("Calendar Photo Inf Controller response: " + forwardResponse.getBody());
        } catch (Exception e) {
            System.err.println("Error forwarding ML response: " + e.getMessage());
        }
    }
}
