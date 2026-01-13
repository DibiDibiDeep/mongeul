package com.example.finalproj.ml.alimML;

import org.springframework.context.ApplicationEvent;


public class AlimMLProcessingCompletedEvent extends ApplicationEvent {

    private final String mlResponse;
    private final Integer alimId;


    public AlimMLProcessingCompletedEvent(Object source, String mlResponse, Integer alimId) {
        super(source);
        this.mlResponse = mlResponse;
        this.alimId = alimId;
    }

    public String getMlResponse() {
        return mlResponse;
    }

    public Integer getAlimId() {
        return alimId;
    }
}