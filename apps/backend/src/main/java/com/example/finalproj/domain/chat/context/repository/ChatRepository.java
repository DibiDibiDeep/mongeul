package com.example.finalproj.domain.chat.context.repository;

import com.example.finalproj.domain.chat.context.entity.ChatMessageDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessageDTO, Long> {


    List<ChatMessageDTO> findByUserIdAndBabyId(Long userId, Long babyId);

    void deleteByUserIdAndBabyId(Long userId, Long babyId);

    List<ChatMessageDTO> findByBabyId(Integer babyId);

    @Query("SELECT c FROM ChatMessageDTO c WHERE c.userId = :userId AND c.babyId = :babyId ORDER BY c.timestamp DESC")
    List<ChatMessageDTO> findRecentMessagesByUserAndBaby(@Param("userId") Long userId,
                                                         @Param("babyId") Long babyId,
                                                         Pageable pageable);
}
