package com.example.finalproj.ml.calendarML;

import org.springframework.context.ApplicationEvent;

public class CalendarMLProcessingCompletedEvent extends ApplicationEvent {
    private final String mlResponse;
    private final Integer calendarPhotoId;

    // 생성자
    public CalendarMLProcessingCompletedEvent(Object source, String mlResponse, Integer calendarPhotoId) {
        super(source);
        this.mlResponse = mlResponse;
        this.calendarPhotoId = calendarPhotoId;
    }

    // ML 응답 getter
    public String getMlResponse() {
        return mlResponse;
    }

    // 캘린더 사진 ID getter
    public Integer getCalendarPhotoId() {
        return calendarPhotoId;
    }
}
