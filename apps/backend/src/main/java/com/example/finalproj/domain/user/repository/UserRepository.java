package com.example.finalproj.domain.user.repository;

import com.example.finalproj.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserIdAndEmail(Integer userId, String email);

    boolean existsById(Integer userId);
}