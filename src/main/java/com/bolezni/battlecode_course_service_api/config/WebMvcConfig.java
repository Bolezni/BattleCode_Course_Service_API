package com.bolezni.battlecode_course_service_api.config;

import com.bolezni.battlecode_course_service_api.security.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("🔄 Registering JwtAuthInterceptor");
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**");
        log.info("🔄 JwtAuthInterceptor registered successfully");
    }
}
