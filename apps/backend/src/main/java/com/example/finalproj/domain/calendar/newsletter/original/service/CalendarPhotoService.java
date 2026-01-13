package com.example.finalproj.domain.calendar.newsletter.original.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.finalproj.domain.calendar.newsletter.original.entity.CalendarPhoto;
import com.example.finalproj.domain.calendar.newsletter.original.repository.CalendarPhotoRepository;
import com.example.finalproj.ml.calendarML.CalendarMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CalendarPhotoService {

    private static final Logger logger = LoggerFactory.getLogger(CalendarPhotoService.class);

    @Autowired
    private CalendarPhotoRepository calendarPhotoRepository;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired

    private CalendarMLService calendarMlService;


    @Value("${aws.s3.bucket}")
    private String bucketName;

    // 모든 CalendarPhoto 레코드를 조회
    public List<CalendarPhoto> getAllCalendarPhotos() {
        return calendarPhotoRepository.findAll();
    }

    // 특정 ID로 CalendarPhoto 조회
    public Optional<CalendarPhoto> getCalendarPhotoById(Integer id) {
        return calendarPhotoRepository.findById(id);
    }

    // 특정 사용자 ID로 CalendarPhoto 목록 조회
    public List<CalendarPhoto> getCalendarPhotosByUserId(Integer userId) {
        return calendarPhotoRepository.findByUserId(userId);
    }

    // 특정 아기 ID로 CalendarPhoto 목록 조회
    public List<CalendarPhoto> getCalendarPhotosByBabyId(Integer babyId) {
        return calendarPhotoRepository.findByBabyId(babyId);
    }

    // 특정 날짜로 CalendarPhoto 목록 조회
    public List<CalendarPhoto> getCalendarPhotosByDate(LocalDateTime date) {
        LocalDate localDate = date.toLocalDate();
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        return calendarPhotoRepository.findByDateBetween(startOfDay, endOfDay);
    }

    // 특정 연도와 월로 CalendarPhoto 목록 조회
    public List<CalendarPhoto> getCalendarPhotosByYearAndMonth(int year, int month) {
        return calendarPhotoRepository.findByYearAndMonth(year, month);
    }

    // CalendarPhoto 생성
    public CalendarPhoto createCalendarPhoto(MultipartFile file, Integer userId, Integer babyId, LocalDateTime date) throws IOException {
        String filePath = uploadFileToS3(file); // S3에 파일 업로드

        CalendarPhoto calendarPhoto = new CalendarPhoto();
        calendarPhoto.setUserId(userId);
        calendarPhoto.setBabyId(babyId);
        calendarPhoto.setFilePath(filePath);
        calendarPhoto.setDate(date);

        CalendarPhoto savedCalendarPhoto = calendarPhotoRepository.save(calendarPhoto);

        // ML 서비스로 이미지 전송 (비동기 처리)
        calendarMlService.sendImageToMLService(filePath, userId, babyId, savedCalendarPhoto.getCalendarPhotoId());

        return savedCalendarPhoto;
    }

    // S3에 파일 업로드
    private String uploadFileToS3(MultipartFile multipartFile) throws IOException {
        File file = convertMultiPartToFile(multipartFile);
        String fileName = generateFileName(multipartFile);
        try {
            logger.info("Uploading file to S3: {}", fileName);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
            String fileUrl = s3Client.getUrl(bucketName, fileName).toString();
            logger.info("File uploaded successfully. URL: {}", fileUrl);
            return fileUrl;
        } finally {
            file.delete();
        }
    }

    // MultipartFile을 File로 변환
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    // 파일 이름 생성
    private String generateFileName(MultipartFile multiPart) {
        return UUID.randomUUID().toString() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    // 특정 ID의 CalendarPhoto 삭제
    public void deleteCalendarPhoto(Integer id) {
        calendarPhotoRepository.deleteById(id);
    }
}
