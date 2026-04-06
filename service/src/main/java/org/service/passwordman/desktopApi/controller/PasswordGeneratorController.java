package org.service.passwordman.desktopApi.controller;

import org.service.passwordman.desktopApi.handler.PasswordGeneratorHandler;

public class PasswordGeneratorController {

    private final PasswordGeneratorHandler passwordGeneratorHandler;

    public PasswordGeneratorController(PasswordGeneratorHandler passwordGeneratorHandler) {
        this.passwordGeneratorHandler = passwordGeneratorHandler;
    }

    public Object generate(
            int length,
            boolean useUppercase,
            boolean useLowercase,
            boolean useDigits,
            boolean useSymbols
    ) {
        return passwordGeneratorHandler.generateSafe(
                length,
                useUppercase,
                useLowercase,
                useDigits,
                useSymbols
        );
    }
}