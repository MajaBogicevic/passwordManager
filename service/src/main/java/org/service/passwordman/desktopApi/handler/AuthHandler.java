package org.service.passwordman.desktopApi.handler;

import java.util.UUID;

import org.service.passwordman.application.port.RefreshTokenStore;
import org.service.passwordman.application.port.TokenService;
import org.service.passwordman.application.security.AuthToken;
import org.service.passwordman.application.security.TokenPayload;
import org.service.passwordman.application.usecase.auth.ChangeLoginPasswordUseCase;
import org.service.passwordman.application.usecase.auth.ChangeMasterPasswordUseCase;
import org.service.passwordman.application.usecase.auth.LoginUserUseCase;
import org.service.passwordman.application.usecase.auth.LogoutUserUseCase;
import org.service.passwordman.application.usecase.auth.RefreshAccessTokenUseCase;
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.request.ChangeLoginPasswordRequest;
import org.service.passwordman.desktopApi.request.ChangeMasterPasswordRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.LogoutRequest;
import org.service.passwordman.desktopApi.request.RefreshTokenRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;
import org.service.passwordman.desktopApi.response.AuthResponse;
import org.service.passwordman.desktopApi.response.LoginResponse;
import org.service.passwordman.desktopApi.response.RefreshTokenResponse;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;
import org.service.passwordman.infrastructure.security.CurrentUserProvider;

public class AuthHandler {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ChangeMasterPasswordUseCase changeMasterPasswordUseCase;
    private final ChangeLoginPasswordUseCase changeLoginPasswordUseCase;
    private final AuthDesktopMapper authDesktopMapper;
    private final AuthRequestValidator authRequestValidator;
    private final ApiHandler apiHandler;
    private final TokenService tokenService;
    private final CurrentUserProvider currentUserProvider;
    private final LogoutUserUseCase logoutUserUseCase;
    private final RefreshTokenStore refreshTokenStore;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    public AuthHandler(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ChangeMasterPasswordUseCase changeMasterPasswordUseCase,
            ChangeLoginPasswordUseCase changeLoginPasswordUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler,
            TokenService tokenService,
            CurrentUserProvider currentUserProvider,
            LogoutUserUseCase logoutUserUseCase,
            RefreshTokenStore refreshTokenStore,
            RefreshAccessTokenUseCase refreshAccessTokenUseCase
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.changeMasterPasswordUseCase = changeMasterPasswordUseCase;
        this.changeLoginPasswordUseCase = changeLoginPasswordUseCase;
        this.authDesktopMapper = authDesktopMapper;
        this.authRequestValidator = authRequestValidator;
        this.apiHandler = apiHandler;
        this.tokenService = tokenService;
        this.currentUserProvider = currentUserProvider;
        this.logoutUserUseCase = logoutUserUseCase;
        this.refreshTokenStore = refreshTokenStore;
        this.refreshAccessTokenUseCase = refreshAccessTokenUseCase;
    }

    public AuthResponse register(RegisterRequest request, String clientIp) {
        authRequestValidator.validateRegister(request);

        registerUserUseCase.execute(
                request.getEmail(),
                request.getUsername(),
                request.getLoginPassword(),
                request.getMasterPassword(),
                request.getNotes(),
                clientIp
        );

        return authDesktopMapper.success("User successfully registered.");
    }

    public Object registerSafe(RegisterRequest request, String clientIp) {
        return apiHandler.execute(() -> register(request, clientIp));
    }

    public LoginResponse login(LoginRequest request, String clientIp) {
        authRequestValidator.validateLogin(request);

        TokenPayload tokenPayload = loginUserUseCase.execute(
                request.getUsername(),
                request.getLoginPassword(),
                clientIp
        );

        String sessionId = UUID.randomUUID().toString();

        String accessToken = tokenService.generateAccessToken(
                new TokenPayload(
                        tokenPayload.getUserId(),
                        tokenPayload.getUsername(),
                        null,
                        sessionId,
                        "access"
                )
        );

        String refreshToken = tokenService.generateRefreshToken(
                new TokenPayload(
                        tokenPayload.getUserId(),
                        tokenPayload.getUsername(),
                        null,
                        sessionId,
                        "refresh"
                )
        );

        TokenPayload refreshPayload = tokenService.parseRefreshToken(refreshToken);
        long refreshExpiresAtMillis = tokenService.extractExpirationMillis(refreshToken);

        refreshTokenStore.save(
                refreshPayload.getJwtTokenId(),
                refreshPayload.getUserId(),
                sessionId,
                refreshExpiresAtMillis
        );

        return new LoginResponse(
                "User successfully logged in.",
                accessToken,
                refreshToken
        );
    }

    public Object loginSafe(LoginRequest request, String clientIp) {
        return apiHandler.execute(() -> login(request, clientIp));
    }

    public AuthResponse changeMasterPassword(ChangeMasterPasswordRequest request, String clientIp) {
        authRequestValidator.validateChangeMasterPassword(request);

        int currentUserId = currentUserProvider.requireUserId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();

        changeMasterPasswordUseCase.execute(
                currentUserId,
                jwtTokenId,
                request.getOldMasterPassword(),
                request.getNewMasterPassword(),
                clientIp
        );

        return authDesktopMapper.success("Master password successfully changed.");
    }

    public Object changeMasterPasswordSafe(ChangeMasterPasswordRequest request, String clientIp) {
        return apiHandler.execute(() -> changeMasterPassword(request, clientIp));
    }

    public AuthResponse changeLoginPassword(ChangeLoginPasswordRequest request, String clientIp) {
        authRequestValidator.validateChangeLoginPassword(request);

        int currentUserId = currentUserProvider.requireUserId();

        changeLoginPasswordUseCase.execute(
                currentUserId,
                request.getOldLoginPassword(),
                request.getNewLoginPassword(),
                clientIp
        );

        return authDesktopMapper.success("Login password successfully changed.");
    }

    public Object changeLoginPasswordSafe(ChangeLoginPasswordRequest request, String clientIp) {
        return apiHandler.execute(() -> changeLoginPassword(request, clientIp));
    }

    public AuthResponse logout(LogoutRequest request, String clientIp) {
        authRequestValidator.validateLogout();

        int currentUserId = currentUserProvider.requireUserId();
        String currentSessionId = currentUserProvider.requireSessionId();
        String jwtTokenId = currentUserProvider.requireJwtTokenId();
        long expiresAtMillis = currentUserProvider.requireAccessTokenExpiresAtMillis();

        logoutUserUseCase.execute(
                currentUserId,
                currentSessionId,
                jwtTokenId,
                expiresAtMillis,
                request != null && request.isAllSessions(),
                clientIp
        );

        return authDesktopMapper.success("User successfully logged out.");
    }

    public Object logoutSafe(LogoutRequest request, String clientIp) {
        return apiHandler.execute(() -> logout(request, clientIp));
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request, String clientIp) {
        authRequestValidator.validateRefreshToken(request);

        AuthToken authTokens = refreshAccessTokenUseCase.execute(
                request.getRefreshToken(),
                clientIp
        );

        return new RefreshTokenResponse(
                "Access token successfully refreshed.",
                authTokens.getAccessToken(),
                authTokens.getRefreshToken()
        );
    }

    public Object refreshTokenSafe(RefreshTokenRequest request, String clientIp) {
        return apiHandler.execute(() -> refreshToken(request, clientIp));
    }
}