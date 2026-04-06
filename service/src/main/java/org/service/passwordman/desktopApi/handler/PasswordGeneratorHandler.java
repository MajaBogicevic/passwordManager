package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.application.usecase.generator.GeneratePasswordUseCase;

public class PasswordGeneratorHandler {

    private final GeneratePasswordUseCase generatePasswordUseCase;
    private final ApiHandler apiHandler;

    public PasswordGeneratorHandler(
            GeneratePasswordUseCase generatePasswordUseCase,
            ApiHandler apiHandler
    ) {
        this.generatePasswordUseCase = generatePasswordUseCase;
        this.apiHandler = apiHandler;
    }

    public String generate(
            int length,
            boolean useUppercase,
            boolean useLowercase,
            boolean useDigits,
            boolean useSymbols
    ) {
        return generatePasswordUseCase.execute(
                length,
                useUppercase,
                useLowercase,
                useDigits,
                useSymbols
        );
    }

    public Object generateSafe(
            int length,
            boolean useUppercase,
            boolean useLowercase,
            boolean useDigits,
            boolean useSymbols
    ) {
        return apiHandler.execute(() -> generate(
                length,
                useUppercase,
                useLowercase,
                useDigits,
                useSymbols
        ));
    }
}