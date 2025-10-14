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
@Lazy
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Lazy
    private final AuthClientService authClientService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        JwtAuth jwtAuth = handlerMethod.getMethodAnnotation(JwtAuth.class);

        if (jwtAuth == null) {
            return true;
        }

        String token = extractToken(request);

        if (token == null && jwtAuth.required()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token required");
            return false;
        }

        if (token != null) {
            try {
                if (!authClientService.validateToken(token)) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
                    return false;
                }

                UserInfo userInfo = authClientService.getUserInfo(token);

                if (!hasRequiredRoles(userInfo, jwtAuth.roles())) {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient permissions");
                    return false;
                }

                request.setAttribute("userInfo", userInfo);

            } catch (Exception e) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Authentication service error: " + e.getMessage());
                return false;
            }
        }

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
