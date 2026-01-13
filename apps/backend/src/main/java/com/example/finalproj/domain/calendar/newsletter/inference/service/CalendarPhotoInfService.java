package com.example.finalproj.domain.calendar.newsletter.inference.service;

import com.example.finalproj.domain.calendar.newsletter.inference.entity.CalendarPhotoInf;
import com.example.finalproj.domain.calendar.newsletter.inference.repository.CalendarPhotoInfRepository;
import com.example.finalproj.domain.calendar.schedule.entity.Calendar;
import com.example.finalproj.domain.calendar.schedule.repository.CalendarRepository;
import com.example.finalproj.domain.baby.original.repository.BabyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class CalendarPhotoInfService {

    @Autowired
    private CalendarPhotoInfRepository calendarPhotoInfRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private BabyRepository babyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 추론 결과를 저장하고 처리하는 메소드
    @Transactional
    public CalendarPhotoInf saveAndProcessInferenceResult(Integer calendarPhotoId, String inferenceResult) {
        CalendarPhotoInf calendarPhotoInf = new CalendarPhotoInf();
        calendarPhotoInf.setCalendarPhotoId(calendarPhotoId);
        calendarPhotoInf.setInferenceResult(inferenceResult);
        calendarPhotoInf.setInferenceDate(LocalDateTime.now());

        // 자료형 오류 수정
        try {
            Map<String, Object> resultMap = objectMapper.readValue(inferenceResult, Map.class);

            // user_id 처리
            Object userIdObj = resultMap.get("user_id");
            if (userIdObj instanceof Integer) {
                calendarPhotoInf.setUserId((Integer) userIdObj);
            } else if (userIdObj instanceof String) {
                calendarPhotoInf.setUserId(Integer.parseInt((String) userIdObj));
            } else {
                throw new IllegalArgumentException("Invalid user_id type");
            }

            // baby_id 처리
            Object babyIdObj = resultMap.get("baby_id");
            if (babyIdObj instanceof Integer) {
                calendarPhotoInf.setBabyId((Integer) babyIdObj);
            } else if (babyIdObj instanceof String) {
                calendarPhotoInf.setBabyId(Integer.parseInt((String) babyIdObj));
            } else {
                throw new IllegalArgumentException("Invalid baby_id type");
            }

            // 추론 결과 저장
            calendarPhotoInf = calendarPhotoInfRepository.save(calendarPhotoInf);

            // 추론 결과를 자동으로 처리
            processCalendarEntries(calendarPhotoInf, resultMap);

        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing inference result", e);
        }

        return calendarPhotoInf;
    }

    // 캘린더 항목을 처리하는 메소드
    private void processCalendarEntries(CalendarPhotoInf calendarPhotoInf, Map<String, Object> resultMap) {
        // year가 null인 경우 현재 년도를 사용
        String year = (String) resultMap.get("year");
        if (year == null) {
            year = String.valueOf(LocalDate.now().getYear());
        }

        String month = (String) resultMap.get("month");
        List<Map<String, Object>> events = (List<Map<String, Object>>) resultMap.get("events");

        for (Map<String, Object> event : events) {
            String date = (String) event.get("date");
            List<Map<String, Object>> activities = (List<Map<String, Object>>) event.get("activities");

            for (Map<String, Object> activity : activities) {
                Calendar calendar = createCalendarFromActivity(calendarPhotoInf, year, month, date, activity);
                saveCalendarIfNotExists(calendar);
            }
        }
    }


    // 활동으로부터 Calendar 객체를 생성하는 메소드
    private Calendar createCalendarFromActivity(CalendarPhotoInf calendarPhotoInf, String year, String month, String date, Map<String, Object> activity) {
        Calendar calendar = new Calendar();
        calendar.setUserId(calendarPhotoInf.getUserId());

        Integer babyId = calendarPhotoInf.getBabyId();
        if (babyId != null && babyRepository.existsById(babyId)) {
            calendar.setBabyId(babyId);
        }

        calendar.setCalendarPhotoId(calendarPhotoInf.getCalendarPhotoId());

        String paddedMonth = String.format("%02d", Integer.parseInt(month));
        String paddedDate = String.format("%02d", Integer.parseInt(date));

        LocalDate eventDate = LocalDate.parse(year + "-" + paddedMonth + "-" + paddedDate, DateTimeFormatter.ISO_DATE);

        // start_time과 end_time 처리
        String startTimeStr = (String) activity.get("start_time");
        String endTimeStr = (String) activity.get("end_time");

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        if (startTimeStr != null && !startTimeStr.isEmpty()) {
            LocalTime startTime = LocalTime.parse(startTimeStr);
            startDateTime = LocalDateTime.of(eventDate, startTime);
        } else {
            startDateTime = eventDate.atStartOfDay();
        }

        if (endTimeStr != null && !endTimeStr.isEmpty()) {
            LocalTime endTime = LocalTime.parse(endTimeStr);
            endDateTime = LocalDateTime.of(eventDate, endTime);
        } else {
            endDateTime = eventDate.atTime(23, 59,59);
        }

        calendar.setStartTime(startDateTime);
        calendar.setEndTime(endDateTime);

        String name = (String) activity.get("name");
        calendar.setTitle(name);

        String information = (String) activity.get("information");
        if (information != null && !information.isEmpty()){
            calendar.setInformation(information);
        } else {
            calendar.setInformation("내용 없음");
        }

        String notes = (String) activity.get("notes");
        if (notes != null && !notes.isEmpty()){
            calendar.setNotes(notes);
        } else {
            calendar.setNotes("내용 없음");
        }

        // location 설정
        String location = (String) activity.get("location");
        calendar.setLocation(location != null ? location : "");

        return calendar;
    }

    // 중복된 캘린더가 없으면 저장하는 메소드
    private void saveCalendarIfNotExists(Calendar calendar) {
        if (!calendarRepository.existsByUserIdAndBabyIdAndStartTimeAndTitle(
                calendar.getUserId(), calendar.getBabyId(), calendar.getStartTime(), calendar.getTitle())) {
            calendarRepository.save(calendar);
        }
    }

    // 모든 CalendarPhotoInf 레코드를 조회하는 메소드
    public List<CalendarPhotoInf> getAllCalendarPhotoInfs() {
        return calendarPhotoInfRepository.findAllCalendarPhotoInfs();
    }

    // 특정 사용자 ID로 CalendarPhotoInf를 조회
    public List<CalendarPhotoInf> getCalendarPhotoInfByUserId(Integer userId) {
        return calendarPhotoInfRepository.findCalendarPhotoInfByUserId(userId);
    }
}
