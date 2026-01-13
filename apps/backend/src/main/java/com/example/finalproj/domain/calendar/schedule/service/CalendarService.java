package com.example.finalproj.domain.calendar.schedule.service;

import com.example.finalproj.domain.calendar.schedule.entity.Calendar;
import com.example.finalproj.domain.calendar.schedule.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {
    @Autowired
    private CalendarRepository calendarRepository;

    // 모든 Calendar 레코드를 조회
    public List<Calendar> getAllCalendars() {
        return calendarRepository.findAll();
    }

    // 특정 ID로 Calendar 레코드를 조회
    public Calendar getCalendarById(Integer calendarId) {
        Optional<Calendar> calendar = calendarRepository.findById(calendarId);
        return calendar.orElse(null);
    }

    // 특정 사용자 ID로 Calendar 목록을 조회
    public List<Calendar> getCalendarsByUserId(Integer userId) {
        return calendarRepository.findByUserId(userId);
    }

    // 특정 아기 ID로 Calendar 목록을 조회
    public List<Calendar> getCalendarsByBabyId(Integer babyId) {
        return calendarRepository.findByBabyId(babyId);
    }

    // 특정 날짜로 Calendar 목록을 조회
    public List<Calendar> getCalendarsByDate(LocalDate date) {
        return calendarRepository.findByStartTimeDate(date);
    }

    // 특정 날짜 범위로 Calendar 목록을 조회
    public List<Calendar> getCalendarsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return calendarRepository.findByStartTimeBetween(startDate, endDate);
    }

    // 새로운 Calendar 레코드를 생성
    public Calendar createCalendar(Calendar calendar) {
        return calendarRepository.save(calendar);
    }

    // 특정 ID로 Calendar 레코드를 수정
    public Calendar updateCalendar(Integer calendarId, Calendar calendarDetails) {
        Optional<Calendar> calendar = calendarRepository.findById(calendarId);

        if (calendar.isPresent()) {
            Calendar updatedCalendar = calendar.get();
            updatedCalendar.setUserId(calendarDetails.getUserId());
            updatedCalendar.setBabyId(calendarDetails.getBabyId());
            updatedCalendar.setCalendarPhotoId(calendarDetails.getCalendarPhotoId());
            updatedCalendar.setTodayId(calendarDetails.getTodayId());
            updatedCalendar.setBookId(calendarDetails.getBookId());
            updatedCalendar.setTitle(calendarDetails.getTitle());
            updatedCalendar.setStartTime(calendarDetails.getStartTime());
            updatedCalendar.setEndTime(calendarDetails.getEndTime());
            updatedCalendar.setLocation(calendarDetails.getLocation());
            return calendarRepository.save(updatedCalendar);
        }
        return null;
    }

    // 특정 ID로 Calendar 레코드를 삭제
    public void deleteCalendar(Integer calendarId) {
        calendarRepository.deleteById(calendarId);
    }

    public List<Calendar> getCalendarsByUserIdAndBabyId(Integer userId, Integer babyId) {
        return calendarRepository.findByUserIdAndBabyId(userId, babyId);
    }
}
