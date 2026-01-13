package com.example.finalproj.domain.baby.original.service;

import com.example.finalproj.domain.baby.original.entity.Baby;
import com.example.finalproj.domain.baby.original.repository.BabyRepository;
import com.example.finalproj.domain.user.entity.User;
import com.example.finalproj.domain.user.repository.UserRepository;
import com.example.finalproj.domain.baby.photo.entity.BabyPhoto;
import com.example.finalproj.domain.baby.photo.repository.BabyPhotoRepository;
import com.example.finalproj.domain.book.cover.entity.Book;
import com.example.finalproj.domain.book.cover.repository.BookRepository;
import com.example.finalproj.domain.calendar.newsletter.inference.entity.CalendarPhotoInf;
import com.example.finalproj.domain.calendar.newsletter.inference.repository.CalendarPhotoInfRepository;
import com.example.finalproj.domain.calendar.newsletter.original.entity.CalendarPhoto;
import com.example.finalproj.domain.calendar.newsletter.original.repository.CalendarPhotoRepository;
import com.example.finalproj.domain.calendar.schedule.entity.Calendar;
import com.example.finalproj.domain.calendar.schedule.repository.CalendarRepository;
import com.example.finalproj.domain.chat.context.entity.ChatMessageDTO;
import com.example.finalproj.domain.chat.context.repository.ChatRepository;
import com.example.finalproj.domain.chat.diary.entity.TodaySum;
import com.example.finalproj.domain.chat.diary.repository.TodaySumRepository;
import com.example.finalproj.domain.memo.original.entity.Memo;
import com.example.finalproj.domain.memo.original.repository.MemoRepository;
import com.example.finalproj.domain.notice.inference.entity.AlimInf;
import com.example.finalproj.domain.notice.inference.repository.AlimInfRepository;
import com.example.finalproj.domain.notice.original.entity.Alim;
import com.example.finalproj.domain.notice.original.repository.AlimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BabyService {
    @Autowired
    private BabyRepository babyRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BabyPhotoRepository babyPhotoRepository;

    @Autowired
    private AlimInfRepository alimInfRepository;

    @Autowired
    private AlimRepository alimRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CalendarPhotoRepository calendarPhotoRepository;

    @Autowired
    private CalendarPhotoInfRepository calendarPhotoInfRepository;

    @Autowired
    private MemoRepository  memoRepository;

    @Autowired
    private TodaySumRepository todaySumRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ChatRepository chatRepository;

    // 모든 아기 정보 조회
    public List<Baby> getAllBabies() {
        return babyRepository.findAll();
    }

    // 특정 아기 정보 조회
    public Optional<Baby> getBabyById(Integer babyId) {
        return babyRepository.findById(babyId);
    }

    // 사용자 ID로 아기 정보 조회
    public List<Baby> getBabyByUserId(Integer userId) {
        return babyRepository.findByUserId(userId);
    }

    // 새로운 아기 정보 생성
    public Baby createBaby(Baby baby) {
        Baby savedBaby = babyRepository.save(baby);
        updateUserHasBabyFlag(baby.getUserId(), true);
        return savedBaby;
    }

    // 아기 정보 수정
    public Baby updateBaby(Integer babyId, Baby babyDetails) {
        Optional<Baby> baby = babyRepository.findById(babyId);
        if (baby.isPresent()) {
            Baby updatedBaby = baby.get();
            updatedBaby.setBabyName(babyDetails.getBabyName());
            updatedBaby.setBirth(babyDetails.getBirth());
            updatedBaby.setGender(babyDetails.getGender());
            updatedBaby.setUserId(babyDetails.getUserId());
            return babyRepository.save(updatedBaby);
        }
        return null;
    }

    // 아기 정보 삭제
    public void deleteBaby(Integer babyId) {
        Baby baby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id: " + babyId));

        // BabyPhoto 레코드를 삭제
        List<BabyPhoto> photos = babyPhotoRepository.findByBabyId(babyId);
        babyPhotoRepository.deleteAll(photos);

        // Alim_inf 레코드 삭제
        List<AlimInf> alimInfs = alimInfRepository.findByBabyId(babyId);
        alimInfRepository.deleteAll(alimInfs);

        // Alim 레코드 삭제
        List<Alim> alims = alimRepository.findByBabyId(babyId);
        alimRepository.deleteAll(alims);

        // Calendar 레코드 삭제
        List<Calendar> calendars = calendarRepository.findByBabyId(babyId);
        calendarRepository.deleteAll(calendars);

        // Calendar Photo 레코드 삭제
        List<CalendarPhoto> calendarPhotos = calendarPhotoRepository.findByBabyId(babyId);
        calendarPhotoRepository.deleteAll(calendarPhotos);

        // Calendar Photo Int 레코드 삭제
        List<CalendarPhotoInf> calendarPhotoInfs = calendarPhotoInfRepository.findByBabyId(babyId);
        calendarPhotoInfRepository.deleteAll(calendarPhotoInfs);

        // Memo 레코드 삭제
        List<Memo> memos = memoRepository.findByBabyId(babyId);
        memoRepository.deleteAll(memos);

        // todaySum 레코드 삭제
        List<TodaySum> todaySums = todaySumRepository.findByBabyId(babyId);
        todaySumRepository.deleteAll(todaySums);

        // Book 레코드 삭제
        List<Book> books = bookRepository.findByBabyId(babyId);
        bookRepository.deleteAll(books);

        // Chat 레코드 삭제
        List<ChatMessageDTO> chatMessageDTOS = chatRepository.findByBabyId(babyId);
        chatRepository.deleteAll(chatMessageDTOS);

        // 다음 Baby 레코드를 삭제
        babyRepository.delete(baby);
    }

    // 사용자가 아기 정보를 가지고 있는지 확인
    public boolean userHasBaby(Integer userId) {
        return babyRepository.existsByUserId(userId);
    }

    public Baby save(Baby baby) {
        return babyRepository.save(baby);
    }

    private void updateUserHasBabyFlag(Integer userId, boolean hasBaby) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setHasBaby(hasBaby);
            userRepository.save(user);
        }
    }
}