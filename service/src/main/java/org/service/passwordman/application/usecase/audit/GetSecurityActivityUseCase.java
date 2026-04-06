package org.service.passwordman.application.usecase.audit;

import org.service.passwordman.domain.model.AuditLog;

import java.util.List;

public interface GetSecurityActivityUseCase {
    List<AuditLog> execute(int userId);
}