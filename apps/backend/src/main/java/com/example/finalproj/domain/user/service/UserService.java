package com.example.finalproj.domain.user.service;

import com.example.finalproj.domain.user.entity.User;
import com.example.finalproj.auth.jwt.JwtTokenProvider;
import com.example.finalproj.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public User findOrCreateUserByEmail(String email, String name) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // 필요한 경우 기존 사용자 정보 업데이트
                    if (!existingUser.getName().equals(name)) {
                        existingUser.setName(name);
                        return userRepository.save(existingUser);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    // 새 사용자 생성
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setNewUser(true);
                    return userRepository.save(newUser);
                });
    }
    public void createDummyUser() {
        // Check if the user already exists
        Optional<User> existingUser = findUserByIdAndEmail(3, "mmongeul@gmail.com");
        if (!existingUser.isPresent()) {
            User dummyUser = new User();
            dummyUser.setUserId(3);
            dummyUser.setEmail("mmongeul@gmail.com");
            dummyUser.setName("Dummy User");
            dummyUser.setNewUser(true); // Adjust as necessary
            // Save the dummy user to the database
            userRepository.save(dummyUser);
        }
    }

    

    // 모든 사용자 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 ID로 사용자 조회
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    // 사용자 정보 업데이트
    public User updateUser(Integer userId, User userDetails) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    return userRepository.save(user);
                })
                .orElse(null);
    }

    // 사용자 삭제
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    // Find user by userId and email
    public Optional<User> findUserByIdAndEmail(Integer userId, String email) {
        return userRepository.findByUserIdAndEmail(userId, email);
    }

    // Check if user is new
    public boolean isNewUser(Optional<User> user) {
        return user.orElseThrow().isNewUser(); // Adjust logic as necessary
    }

    public String generateJwtToken(User user) {
        return jwtTokenProvider.generateJwtToken(user);
    }
}
