package org.service.passwordman.application.service.generator;

import org.service.passwordman.application.usecase.generator.GeneratePasswordUseCase;

import java.security.SecureRandom;

public class GeneratePasswordService implements GeneratePasswordUseCase {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.<>?";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String execute(
            int length,
            boolean useUppercase,
            boolean useLowercase,
            boolean useDigits,
            boolean useSymbols
    ) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than 0.");
        }

        StringBuilder alphabet = new StringBuilder();

        if (useUppercase) {
            alphabet.append(UPPERCASE);
        }
        if (useLowercase) {
            alphabet.append(LOWERCASE);
        }
        if (useDigits) {
            alphabet.append(DIGITS);
        }
        if (useSymbols) {
            alphabet.append(SYMBOLS);
        }

        if (alphabet.isEmpty()) {
            throw new IllegalArgumentException("At least one character group must be selected.");
        }

        String chars = alphabet.toString();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}