package org.service.passwordman.infrastructure.config;

import org.service.passwordman.application.port.Clock;

import java.time.LocalDateTime;

public class SystemClockAdapter implements Clock {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}