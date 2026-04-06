package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.auth.ChangeMasterPasswordUseCase;
import org.service.passwordman.application.usecase.auth.ChangeLoginPasswordUseCase;
import org.service.passwordman.application.usecase.auth.LoginUserUseCase;
import org.service.passwordman.application.usecase.auth.RegisterUserUseCase;
import org.service.passwordman.desktopApi.mapper.AuthDesktopMapper;
import org.service.passwordman.desktopApi.request.ChangeMasterPasswordRequest;
import org.service.passwordman.desktopApi.request.ChangeLoginPasswordRequest;
import org.service.passwordman.desktopApi.request.LoginRequest;
import org.service.passwordman.desktopApi.request.RegisterRequest;
import org.service.passwordman.desktopApi.response.AuthResponse;
import org.service.passwordman.desktopApi.validation.AuthRequestValidator;


public class AuthHandler {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ChangeMasterPasswordUseCase changeMasterPasswordUseCase;
    private final ChangeLoginPasswordUseCase changeLoginPasswordUseCase;
    private final AuthDesktopMapper authDesktopMapper;
    private final AuthRequestValidator authRequestValidator;
    private final ApiHandler apiHandler;

    public AuthHandler(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ChangeMasterPasswordUseCase changeMasterPasswordUseCase,
            ChangeLoginPasswordUseCase changeLoginPasswordUseCase,
            AuthDesktopMapper authDesktopMapper,
            AuthRequestValidator authRequestValidator,
            ApiHandler apiHandler
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.changeMasterPasswordUseCase = changeMasterPasswordUseCase;
        this.changeLoginPasswordUseCase = changeLoginPasswordUseCase;
        this.authDesktopMapper = authDesktopMapper;
        this.authRequestValidator = authRequestValidator;
        this.apiHandler = apiHandler;
    }

    public AuthResponse register(RegisterRequest request) {
        authRequestValidator.validateRegister(request);

        registerUserUseCase.execute(
                request.getEmail(),
                request.getUsername(),
                request.getLoginPassword(),
                request.getMasterPassword(),
                request.getNotes(),
                request.getIpAddress()
        );

        return authDesktopMapper.success("User successfully registered.");
    }

    public Object registerSafe(RegisterRequest request) {
        return apiHandler.execute(() -> register(request));
    }

    public AuthResponse login(LoginRequest request) {
        authRequestValidator.validateLogin(request);

        loginUserUseCase.execute(
                request.getUsername(),
                request.getLoginPassword(),
                request.getIpAddress()
        );

        return authDesktopMapper.success("User successfully logged in.");
    }

    public Object loginSafe(LoginRequest request) {
        return apiHandler.execute(() -> login(request));
    }

    public AuthResponse changeMasterPassword(ChangeMasterPasswordRequest request) {
        authRequestValidator.validateChangeMasterPassword(request);

        changeMasterPasswordUseCase.execute(
                request.getUserId(),
                request.getOldMasterPassword(),
                request.getNewMasterPassword(),
                request.getIpAddress()
        );

        return authDesktopMapper.success("Master password successfully changed.");
    }

    public Object changeMasterPasswordSafe(ChangeMasterPasswordRequest request) {
        return apiHandler.execute(() -> changeMasterPassword(request));
    }

    public AuthResponse changeLoginPasswordSafe(ChangeLoginPasswordRequest request) {
        authRequestValidator.validateChangeLoginPassword(request);

        changeLoginPasswordUseCase.execute(
                request.getUserId(),
                request.getOldLoginPassword(),
                request.getNewLoginPassword(),
                request.getIpAddress()
        );

        return authDesktopMapper.success("Login password successfully changed.");
    }
}