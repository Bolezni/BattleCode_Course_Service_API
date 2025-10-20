package com.bolezni.battlecode_course_service_api.security;

import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.sevice.AuthClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Lazy
    private final AuthClientService authClientService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        log.info("=== JWT AUTH INTERCEPTOR START ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Handler type: {}", handler.getClass().getName());

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            log.info("Not a HandlerMethod, skipping authentication");
            return true;
        }

        log.info("Method: {}", handlerMethod.getMethod().getName());

        // Проверяем аннотацию на методе
        JwtAuth jwtAuth = handlerMethod.getMethodAnnotation(JwtAuth.class);
        log.info("JwtAuth annotation found on method: {}", jwtAuth != null);

        // Если на методе нет, проверяем на классе
        if (jwtAuth == null) {
            jwtAuth = handlerMethod.getBeanType().getAnnotation(JwtAuth.class);
            log.info("JwtAuth annotation found on class: {}", jwtAuth != null);
        }

        if (jwtAuth == null) {
            log.info("No JwtAuth annotation found, skipping authentication");
            return true;
        }

        log.info("JwtAuth configuration - required: {}, roles: {}",
                jwtAuth.required(), Arrays.toString(jwtAuth.roles()));

        String token = extractToken(request);
        log.info("Token extracted: {}", token != null ? "YES" : "NO");

        if (token == null && jwtAuth.required()) {
            log.warn("❌ Token required but not provided");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token required");
            return false;
        }

        if (token != null) {
            try {
                log.info("🔐 Starting token validation...");
                boolean isValid = authClientService.validateToken(token);
                log.info("✅ Token validation result: {}", isValid);

                if (!isValid) {
                    log.warn("❌ Token validation failed");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
                    return false;
                }

                UserInfo userInfo = authClientService.getUserInfo(token);
                log.info("✅ User authenticated: {} (ID: {})", userInfo.username(), userInfo.userId());
                log.info("✅ User roles: {}", userInfo.roles());

                if (!hasRequiredRoles(userInfo, jwtAuth.roles())) {
                    log.warn("❌ User {} has insufficient roles. Has: {}, Required: {}",
                            userInfo.username(), userInfo.roles(), Arrays.toString(jwtAuth.roles()));
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient permissions");
                    return false;
                }

                request.setAttribute("userInfo", userInfo);
                log.info("✅ UserInfo successfully set in request attributes");

            } catch (Exception e) {
                log.error("❌ Authentication service error", e);
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Authentication service error: " + e.getMessage());
                return false;
            }
        }

        log.info("=== JWT AUTH INTERCEPTOR END - SUCCESS ===");
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean hasRequiredRoles(UserInfo userInfo, String[] requiredRoles) {
        if (requiredRoles.length == 0) return true;
        return userInfo.roles().stream()
                .anyMatch(role -> Arrays.asList(requiredRoles).contains(role));
    }
}