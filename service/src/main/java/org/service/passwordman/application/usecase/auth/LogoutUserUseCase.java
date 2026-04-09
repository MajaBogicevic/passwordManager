package org.service.passwordman.application.usecase.auth;

public interface LogoutUserUseCase {

    void execute(
            int userId,
            String sessionId,
            String jwtTokenId,
            long expiresAtMillis,
            boolean allSessions,
            String ipAddress
    );
}