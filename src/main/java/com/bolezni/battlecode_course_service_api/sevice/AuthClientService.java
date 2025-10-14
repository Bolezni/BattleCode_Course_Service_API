package com.bolezni.battlecode_course_service_api.sevice;

import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.grpc.AuthServiceGrpc;
import com.bolezni.battlecode_course_service_api.grpc.TokenRequest;
import com.bolezni.battlecode_course_service_api.grpc.TokenResponse;
import com.bolezni.battlecode_course_service_api.grpc.UserResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@Lazy
public class AuthClientService {

    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authStub;

    public boolean validateToken(String token) {
        try {
            log.debug("Validating token via gRPC");
            TokenRequest request = TokenRequest.newBuilder()
                    .setToken(token)
                    .build();

            TokenResponse response = authStub.validateToken(request);
            boolean isValid = response.getValid();
            log.debug("Token validation result: {}", isValid);
            return isValid;

        } catch (StatusRuntimeException e) {
            log.error("Auth service unavailable: {}", e.getStatus());
            throw new RuntimeException("Auth service unavailable: " + e.getStatus(), e);
        }
    }

    public UserInfo getUserInfo(String token) {
        try {
            log.debug("Getting user info via gRPC");
            TokenRequest request = TokenRequest.newBuilder()
                    .setToken(token)
                    .build();

            UserResponse response = authStub.getUserFromToken(request);

            if (!response.getError().isEmpty()) {
                throw new RuntimeException("Auth error: " + response.getError());
            }

            UserInfo userInfo = new UserInfo(
                    response.getUserId(),
                    response.getUsername(),
                    response.getEmail(),
                    new ArrayList<>(response.getRolesList()),
                    response.getAvatarUrl(),
                    response.getBio(),
                    response.getIsVerified()
            );

            log.debug("Retrieved user info: {}", userInfo.username());
            return userInfo;

        } catch (StatusRuntimeException e) {
            log.error("Failed to get user info: {}", e.getStatus());
            throw new RuntimeException("Auth service unavailable: " + e.getStatus(), e);
        }
    }
}
