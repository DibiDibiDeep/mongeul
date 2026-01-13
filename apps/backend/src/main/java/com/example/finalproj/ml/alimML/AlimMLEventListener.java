package com.example.finalproj.ml.alimML;

import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.domain.notice.inference.service.AlimInfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AlimMLEventListener {

    private final RestTemplate restTemplate;
    private final String appUrl;
    private final AlimInfService alimInfService;
    private final ObjectMapper objectMapper;

    public AlimMLEventListener(RestTemplate restTemplate,
                               @Value("${app.url:http://localhost:8080}") String appUrl,
                               AlimInfService alimInfService,
                               ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.appUrl = appUrl;
        this.alimInfService = alimInfService;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void handleAlimMLProcessingCompletedEvent(AlimMLProcessingCompletedEvent event) {
        String mlResponse = event.getMlResponse();
        Integer alimId = event.getAlimId();

        try {
            Map<String, Object> responseMap = objectMapper.readValue(mlResponse, Map.class);
            responseMap.put("alim_id", alimId);  // Add alimId to the map

            AlimInf alimInf = alimInfService.createAlimInf(responseMap);

            System.out.println("AlimInf created and saved successfully: " + alimInf.getAlimInfId());
        } catch (Exception e) {
            System.err.println("Error processing Alim ML response from event: " + e.getMessage());
        }
    }
}