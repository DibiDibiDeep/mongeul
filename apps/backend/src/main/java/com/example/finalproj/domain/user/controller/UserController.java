package com.example.finalproj.domain.user.controller;

import com.example.finalproj.domain.baby.original.service.BabyService;
import com.example.finalproj.domain.user.entity.User;
import com.example.finalproj.auth.jwt.JwtTokenProvider;
import com.example.finalproj.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final BabyService babyService;
    private  String clientId = "";
    private  String clientSecret="";
    private  String redirectUri="";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${front.url}")
    private String frontUrl;  // 환경 변수로 주입

    @Autowired
    public UserController(UserService userService,
                          BabyService babyService,
                          @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
                          @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret,
                          @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") String redirectUri) {
        this.userService = userService;
        this.babyService = babyService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    // 자체 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String userIdString = loginData.get("userId");
        String userEmail = loginData.get("email");

        logger.info("Attempting to log in user with ID: {} and Email: {}", userIdString, userEmail);

        try {
            Integer userId = Integer.valueOf(userIdString);
            Optional<User> optionalUser = userService.findUserByIdAndEmail(userId, userEmail);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                boolean isNewUser = user.isNewUser();

                Map<String, Object> response = new HashMap<>();
                response.put("redirectUrl", isNewUser ? "/initialSetting" : "/home");
                response.put("user", user);

                logger.info("User logged in successfully: {}", userId);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed: User not found for ID: {} and Email: {}", userId, userEmail);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user ID or email.");
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", userIdString);
            return ResponseEntity.badRequest().body("User ID must be a valid number.");
        } catch (Exception e) {
            logger.error("Error during login attempt", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
        }
    }


//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
//    private String redirectUri;


//    // OAuth URL 생성 시 포함
//    String authUrl = "https://accounts.google.com/o/oauth2/auth" +
//            "?client_id=" + clientId +
//            "&redirect_uri=" + "http://localhost:8080/api/auth/google-callback" + // 여기에서 redirectUri가 null이면 안 됩니다.
//            "&response_type=code" +
//            "&scope=email%20profile";

    // Google 사용자 인증
//    @PostMapping("/google")
//    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> tokenMap) {
//        logger.info("Attempting to authenticate Google user");
//        try {
//            String token = tokenMap.get("token");
//            if (token == null || token.isEmpty()) {
//                logger.warn("Received empty token for Google authentication");
//                return ResponseEntity.badRequest().body("Token is required");
//            }
//
//            Map<String, Object> response = userService.authenticateGoogleUser(token);
//            User user = (User) response.get("user");
//            if (user == null) {
//                logger.warn("User authentication failed");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User authentication failed");
//            }
//
//            boolean hasBaby = babyService.userHasBaby(user.getUserId());
//
//            Map<String, Object> finalResponse = new HashMap<>(response);
//            finalResponse.put("user", user);
//            finalResponse.put("hasBaby", hasBaby);
//
//            logger.info("User successfully authenticated: {}", user.getUserId());
//            return ResponseEntity.ok(finalResponse);
//        } catch (Exception e) {
//            logger.error("Error during Google authentication", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred during authentication: " + e.getMessage());
//        }
//    }

    // Google 인증 URL 반환
    @GetMapping("/google-url")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl() {
        logger.info("Google OAuth URL generation: clientId={}, redirectUri={}", clientId, redirectUri);

        String authUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=email%20profile";

        Map<String, String> response = new HashMap<>();
        response.put("url", authUrl);
        return ResponseEntity.ok(response);
    }


    // Google Callback 처리
    @GetMapping("/google-callback")
    public void handleGoogleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        try {
            String tokenResponse = exchangeCodeForToken(code);
            Map<String, Object> userInfo = getUserInfo(tokenResponse);

            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            User user = userService.findOrCreateUserByEmail(email, name);

            String jwtToken = userService.generateJwtToken(user);

            // 프론트엔드 URL로 리다이렉트 (JWT 토큰을 쿼리 파라미터로 포함)
            String redirectUrl = frontUrl + "/auth/token/set?token=" + URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error("Error handling Google callback", e);
            response.sendRedirect(frontUrl + "/auth/login?error=" + URLEncoder.encode("Authentication failed", StandardCharsets.UTF_8));
        }
    }

    private String exchangeCodeForToken(String code) throws Exception {
        // Google에 토큰 요청을 보내는 로직
        String tokenUrl = "https://oauth2.googleapis.com/token";
        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "code=" + code +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret + // 비밀 키를 사용해야 함
                "&redirect_uri=" + redirectUri +
                "&grant_type=authorization_code";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to exchange code for token: " + response.body());
        }

        return response.body();
    }

    public String generateJwtToken(User user) {
        return jwtTokenProvider.generateJwtToken(user);
    }

    private Map<String, Object> getUserInfo(String tokenResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> tokenMap = mapper.readValue(tokenResponse, Map.class);
            String accessToken = tokenMap.get("access_token");

            RestTemplate restTemplate = new RestTemplate();
            String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);

            return mapper.readValue(response.getBody(), Map.class); // Map containing user info
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info", e);
        }
    }

//    private String generateJwtToken(Map<String, Object> userInfo) {
//        // Implement JWT generation logic using the userInfo map.
//        // This could include adding claims like user ID, email, roles, etc.
//        // You might want to use a library like JJWT or Spring Security's JWT support.
//
//        // For example:
//        String email = (String) userInfo.get("email");
//        String name = (String) userInfo.get("name");
//
//        // Pseudo code for generating JWT:
//        // return JWT.create()
//        //          .withSubject(email)
//        //          .withClaim("name", name)
//        //          .withExpiresAt(expirationDate)
//        //          .sign(algorithm);
//
//        // For now, return a dummy token for illustration purposes
//        return "jwt_token_" + System.currentTimeMillis();
//    }



//    @PostMapping("/dev-login")
//    public ResponseEntity<?> devLogin() {
//        logger.info("Development login endpoint called");
//        try {
//            // 더미 데이터 생성
//            User dummyUser = new User();
//            dummyUser.setUserId(5);
//            dummyUser.setEmail("dummy@example.com");
//            dummyUser.setName("Dummy User");
//
//            // 더미 액세스 토큰 생성
//            String accessToken = "dummy_access_token_" + System.currentTimeMillis();
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("user", dummyUser);
//            response.put("hasBaby", true);
//            response.put("babyId", 6);
//            response.put("accessToken", accessToken);
//
//            logger.info("Dummy user created with ID: {}", dummyUser.getUserId());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            logger.error("Error in development login", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred during development login: " + e.getMessage());
//        }
//    }

//    @PostMapping("/{userId}/accept-privacy-policy")
//    public ResponseEntity<?> acceptPrivacyPolicy(@PathVariable Integer userId) {
//        try {
//            User updatedUser = userService.acceptPrivacyPolicy(userId);
//            return ResponseEntity.ok(updatedUser);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    // 사용자 ID로 사용자 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Integer userId) {
        logger.info("Fetching user with ID: {}", userId);
        try {
            return userService.getUserById(userId)
                    .map(user -> {
                        logger.info("User found: {}", userId);
                        return ResponseEntity.ok(user);
                    })
                    .orElseGet(() -> {
                        logger.warn("User not found: {}", userId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the user");
        }
    }

    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logger.info("Fetching all users");
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Fetched {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching users");
        }
    }

    // 사용자 정보 업데이트
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Integer userId, @RequestBody User userDetails) {
        logger.info("Updating user with ID: {}", userId);
        try {
            User updatedUser = userService.updateUser(userId, userDetails);
            if (updatedUser == null) {
                logger.warn("User not found for update: {}", userId);
                return ResponseEntity.notFound().build();
            }
            logger.info("User updated successfully: {}", userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user");
        }
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        logger.info("Deleting user with ID: {}", userId);
        try {
            userService.deleteUser(userId);
            logger.info("User deleted successfully: {}", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the user");
        }
    }

    // 미들웨어 사용자 인증
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestBody Map<String, String> tokenMap) {
        logger.info("Validating token");

        String token = tokenMap.get("token");

        if (token == null || token.isEmpty()) {
            logger.warn("Token is missing or empty");
            return ResponseEntity.badRequest().body(createResponse(false));
        }

        boolean isValid = jwtTokenProvider.validateToken(token);
        logger.info("Token validation result: {}", isValid);

        return ResponseEntity.ok(createResponse(isValid));
    }

    private Map<String, Boolean> createResponse(boolean isValid) {
        Map<String, Boolean> response = new HashMap<String, Boolean>();
        response.put("isValid", isValid);
        return response;
    }
}
