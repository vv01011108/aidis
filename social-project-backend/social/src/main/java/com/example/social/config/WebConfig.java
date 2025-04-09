package com.example.social.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "uploads" 폴더 내 파일을 외부에서 접근할 수 있도록 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // 로컬 파일 시스템에서 "uploads" 폴더를 찾음
    }
}
