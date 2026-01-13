package com.example.finalproj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대해 CORS 매핑을 추가합니다.
        registry.addMapping("/**")
                // 모든 origin을 허용합니다.
                .allowedOrigins("*")
                // 허용할 HTTP 메소드를 지정합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 모든 헤더를 허용합니다.
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                // 자격 증명은 비활성화합니다 (allowedOrigins("*")와 함께 사용할 수 없음).
                .allowCredentials(false)
                // preflight 요청의 응답 캐시 시간(초 단위)을 지정합니다.
                .maxAge(3600);  // 1시간 동안 preflight 응답을 캐시함
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // http에서 https로 리다이렉션
//        registry.addRedirectViewController("http://mongeul.com/oauth2/authorization/google",
//                "https://mongeul.com/oauth2/authorization/google");
//    }
}
