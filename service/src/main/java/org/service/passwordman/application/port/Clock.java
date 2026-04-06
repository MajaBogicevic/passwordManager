package org.service.passwordman.application.port;

import java.time.LocalDateTime;

public interface Clock {

    LocalDateTime now();
}