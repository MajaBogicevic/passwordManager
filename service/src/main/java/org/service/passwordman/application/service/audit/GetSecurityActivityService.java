package org.service.passwordman.application.service.audit;

import org.service.passwordman.application.usecase.audit.GetSecurityActivityUseCase;
import org.service.passwordman.domain.exception.UnauthorizedVaultAccessException;
import org.service.passwordman.domain.model.AuditLog;
import org.service.passwordman.domain.repository.AuditLogRepository;
import org.service.passwordman.domain.repository.UserRepository;

import java.util.List;

public class GetSecurityActivityService implements GetSecurityActivityUseCase {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public GetSecurityActivityService(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AuditLog> execute(int userId) {
        userRepository.findById(userId)
                .orElseThrow(UnauthorizedVaultAccessException::new);

        return auditLogRepository.findByUserId(userId);
    }
}