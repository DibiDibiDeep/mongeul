package com.example.finalproj.ml.voiceML;

import org.springframework.context.ApplicationEvent;

public class VoiceMLProcessingCompletedEvent extends ApplicationEvent {
    private final String mlResponse;
    private final Integer userId;
    private final Integer babyId;

    public VoiceMLProcessingCompletedEvent(Object source, String mlResponse, Integer userId, Integer babyId) {
        super(source);
        this.mlResponse = mlResponse;
        this.userId = userId;
        this.babyId = babyId;
    }

    public String getMlResponse() {
        return mlResponse;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getBabyId() {
        return babyId;
    }
}