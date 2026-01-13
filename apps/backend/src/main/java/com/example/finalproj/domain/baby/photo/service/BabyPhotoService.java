package com.example.finalproj.domain.baby.photo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import com.example.finalproj.domain.baby.photo.repository.BabyPhotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BabyPhotoService {

    // BabyPhotoRepository 주입
    @Autowired
    private BabyPhotoRepository babyPhotoRepository;

    // AmazonS3 클라이언트 주입
    @Autowired
    private AmazonS3 s3Client;

    // S3 버킷 이름 주입
    @Value("${aws.s3.bucket}")
    private String bucketName;

    // 모든 아기 사진 조회
    public List<BabyPhoto> getAllBabyPhotos() {
        return babyPhotoRepository.findAll();
    }

    // ID로 특정 아기 사진 조회
    public Optional<BabyPhoto> getBabyPhotoById(Integer id) {
        return babyPhotoRepository.findById(id);
    }

    // 아기 ID로 해당 아기의 모든 사진 조회
    public List<BabyPhoto> getBabyPhotosByBabyId(Integer babyId) {
        return babyPhotoRepository.findByBabyId(babyId);
    }

    // 업로드 날짜로 아기 사진 조회
    public List<BabyPhoto> getBabyPhotosByUploadDate(LocalDateTime uploadDate) {
        return babyPhotoRepository.findByUploadDate(uploadDate);
    }

    // 새로운 아기 사진 생성 및 S3에 업로드
    @Transactional
    public BabyPhoto createOrUpdateBabyPhoto(MultipartFile file, Integer babyId, Integer userId) throws IOException {
        // 기존 사진 찾기
        BabyPhoto existingPhoto = (BabyPhoto) babyPhotoRepository.findTopByBabyIdOrderByUploadDateDesc(babyId).orElse(null);

        // 새 사진 업로드
        String fileName = generateFileName(file);
        String fileUrl = uploadFileToS3(file, fileName);

        if (existingPhoto != null) {
            // 기존 사진 S3에서 삭제
            deleteFileFromS3(existingPhoto.getFilePath());

            // 기존 사진 정보 업데이트
            existingPhoto.setFilePath(fileUrl);
            existingPhoto.setUploadDate(LocalDateTime.now());
            return babyPhotoRepository.save(existingPhoto);
        } else {
            // 새 사진 정보 생성
            BabyPhoto newPhoto = new BabyPhoto();
            newPhoto.setUserId(userId);
            newPhoto.setBabyId(babyId);
            newPhoto.setFilePath(fileUrl);
            newPhoto.setUploadDate(LocalDateTime.now());
            return babyPhotoRepository.save(newPhoto);
        }
    }

    private String uploadFileToS3(MultipartFile file, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    private void deleteFileFromS3(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
    }

    // 아기 사진 삭제
    public void deleteBabyPhoto(Integer id) {
        babyPhotoRepository.deleteById(id);
    }

    public Optional<BabyPhoto> updateBabyPhoto(Integer babyPhotoId, MultipartFile file, Integer babyId, Integer userId) throws IOException {
        Optional<BabyPhoto> existingPhotoOpt = babyPhotoRepository.findById(babyPhotoId);

        if (existingPhotoOpt.isPresent()) {
            BabyPhoto existingPhoto = existingPhotoOpt.get();

            // 파일 처리 로직 (예: S3에 업로드)
            String fileName = generateFileName(file);
            String fileUrl = uploadFileToS3(file, fileName); // 이 메소드는 별도로 구현해야 합니다

            existingPhoto.setFilePath(fileUrl);
            existingPhoto.setUploadDate(LocalDateTime.now());
            existingPhoto.setBabyId(babyId);
            if (userId != null) {
                existingPhoto.setUserId(userId);
            }

            return Optional.of(babyPhotoRepository.save(existingPhoto));
        }

        return Optional.empty();
    }

    public List<BabyPhoto> getBabyPhotosByUserIdAndBabyId(Integer userId, Integer babyId) {
        return babyPhotoRepository.findByUserIdAndBabyId(userId, babyId);
    }

    // 아기 사진 수정
//    public BabyPhoto updateBabyPhoto(MultipartFile file, Integer babyId) throws IOException {
//        String fileUrl = uploadFileToS3(file);
//
//        BabyPhoto babyPhoto = new BabyPhoto();
//        babyPhoto.setBabyId(babyId);
//        babyPhoto.setFilePath(fileUrl);
//        babyPhoto.setUploadDate(LocalDateTime.now());
//
//        return babyPhotoRepository.edit(babyPhoto);
//    }
}