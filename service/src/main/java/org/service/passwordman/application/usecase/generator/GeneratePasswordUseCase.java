package org.service.passwordman.application.usecase.generator;

public interface GeneratePasswordUseCase {
    String execute(int length, boolean useUppercase, boolean useLowercase, boolean useDigits, boolean useSymbols);
}